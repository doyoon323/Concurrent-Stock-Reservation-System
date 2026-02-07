package com.example.reservation_system.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ProductResponse{
    private Long id;
    private String name;
    private Integer price;
    private StockResponse stock;

    @Getter
    @AllArgsConstructor
    public static class StockResponse {
        private int quantity;
    }
}