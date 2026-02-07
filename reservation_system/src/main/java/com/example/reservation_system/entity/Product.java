package com.example.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private String type;

    @Column(nullable = false)
    private Integer price;

    // 1:1 관계의 주인은 Stock에게 맡깁니다 (mappedBy)
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Stock stock;

    @Builder
    public Product(String name, String type, Integer price) {
        this.name = name;
        this.type = type;
        this.price = price;
    }
    /// DTO
    
    public void assignStock(Stock stock){
        this.stock = stock;
        stock.assignStock(this);
    }   
}