package com.example.reservation_system.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {
    @NotBlank(message = "상품 이름은 필수입니다.")
    private String name;

    @Min(value = 0, message = "가격은 0원 이상이어야 합니다.")
    private int price;

    @Min(value = 0, message = "초기 재고는 0개 이상이어야 합니다.")
    private int amount;
    
    private String type;
}
