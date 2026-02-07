package com.example.reservation_system.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.reservation_system.dto.request.ProductCreateRequest;
import com.example.reservation_system.dto.response.ProductResponse;
import com.example.reservation_system.entity.Product;
import com.example.reservation_system.service.ProductService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<Long> createProduct(@RequestBody ProductCreateRequest request) {
        Long productId = productService.createProduct(
                request.getName(),
                request.getPrice(), 
                request.getAmount()
        );
        return ResponseEntity.ok(productId);
    }

    // 재고 수정 API: PATCH /api/products/1/stock?quantity=100
    @PatchMapping("/{id}/stock")
    public ResponseEntity<Void> updateStock(
            @PathVariable Long id, 
            @RequestParam int quantity) {
        productService.updateStockQuantity(id, quantity);
        return ResponseEntity.ok().build();
    }

    // ProductController.java 에 추가

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        
        // DTO 조립
        ProductResponse response = ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .stock(new ProductResponse.StockResponse(product.getStock().getQuantity()))
                .build();
                
        return ResponseEntity.ok(response);
    }

}
