package com.example.reservation_system.dto.response;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ReservationResponse {
    private Long reservationId;
    private String productName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Integer count;
}
