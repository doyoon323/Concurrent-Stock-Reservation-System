package com.example.reservation_system.entity;



import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // DB에 외래키(FK) 컬럼이 생깁니다
    private Product product;

    @Builder
    public Stock(Integer quantity, Product product) {
        this.quantity = quantity;
        this.product = product;
    }

    /// 낙관적 락 
    @Version
    private Long version; // JPA가 이 필드를 보고 자동으로 버전 관리 쿼리 생성 


    // Service가 아닌 Entity에서 정의한 이유 
     /// Stock의 핵심 책임이기 때문. 

    public void decreaseQuantity(int amount){
        if (this.quantity < amount){
            throw new IllegalArgumentException("재고가 부족합니다");
        }
        this.quantity -= amount;
    }

    
    public void assignStock(Product product){
        this.product = product;
    }   

    // 관리자가 재고 추가할 때 사용 
    public void increaseQuantity(int amount){
        this.quantity += amount;
    }

    // 관리자가 재고 설정할 때 사용
    public void setQuantity(int amount){
        if (quantity < 0) {
            throw new IllegalArgumentException("재고는 0개 이상이어야 합니다.");
        }   
        this.quantity = amount;
    }
}