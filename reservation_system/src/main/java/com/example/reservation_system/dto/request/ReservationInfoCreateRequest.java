package com.example.reservation_system.dto.request;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class ReservationInfoCreateRequest {
    private Long productId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
