package com.example.reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation_info")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_info_id") 
    private Long id;

    @Column(name = "reservation_start", nullable = false)
    private LocalDateTime reservationStart;

    @Column(name = "reservation_end", nullable = false)
    private LocalDateTime reservationEnd;

    // 설계도대로 Product와 N:1 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Builder
    public ReservationInfo(LocalDateTime reservationStart, LocalDateTime reservationEnd, Product product) {
        this.reservationStart = reservationStart;
        this.reservationEnd = reservationEnd;
        this.product = product;
    }
}