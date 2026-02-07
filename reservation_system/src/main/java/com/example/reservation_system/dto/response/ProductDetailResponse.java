package com.example.reservation_system.dto.response;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter @Setter
public class ProductDetailResponse {
    private Long id;
    private String name;
    private Integer price; // primitive가 아니라 wrapper클래스를 사용한다. 기본값이 0이 아니라 null이며, 값 (데이터) 자체가 아니라 값을 감싸고 있는 객체이다. 
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}