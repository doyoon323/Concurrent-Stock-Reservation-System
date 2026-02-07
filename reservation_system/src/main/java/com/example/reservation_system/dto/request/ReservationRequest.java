package com.example.reservation_system.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ReservationRequest {
    private Long userId;
    private Long productId;
    private Integer amount;
}