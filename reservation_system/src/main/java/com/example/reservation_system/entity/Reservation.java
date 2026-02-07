package com.example.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservations")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @Column(nullable = false)
    private int amount;

    @Column(nullable = false)
    private LocalDateTime reservationTime;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private String status; // 예: "PENDING", "CONFIRMED", "CANCELLED"

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id") // 유저와 다대일 관계
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id") // 상품과 다대일 관계
    private Product product;

    @Builder
    public Reservation(User user, Product product, String status, int amount, LocalDateTime expiryDate) {
        this.user = user;
        this.product = product;
        this.amount = amount;
        this.expiryDate = expiryDate;
        this.status = status;
        this.reservationTime = LocalDateTime.now();
    }

    /**
     * 최종 구매 확정 시 수량 변경 및 상태 변경
     */
    public void confirm(int finalAmount){
        this.amount = finalAmount;
        this.status = "CONFIRMED";
    }

    /**
     * 취소 처리
     */

    public void cancel(){
        this.status = "CANCELLED";
    }
}