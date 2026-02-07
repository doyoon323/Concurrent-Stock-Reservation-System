package com.example.reservation_system.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.reservation_system.entity.Product;
import com.example.reservation_system.entity.Stock;
import com.example.reservation_system.repository.ProductRepository;
import com.example.reservation_system.repository.StockRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {
    
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final StringRedisTemplate redisTemplate; // Redis 연동을 위해 주입


    @Transactional
    public Long createProduct(String name, int price, int initialQuantity){
        
        if (productRepository.existsByName(name)) {
            throw new IllegalStateException("이미 등록된 상품 이름입니다: " + name);
        }

        Product product = Product.builder()
                .name(name)
                .price(price)
                .build();

        Stock stock = Stock.builder().quantity(initialQuantity).build();

        product.assignStock(stock);
        return productRepository.save(product).getId();
    }

    @Transactional
    public void updateStockQuantity(Long productId, int newQuantity) {
        // 1. 상품 존재 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        // 2. 재고 정보 가져오기 (Product 엔티티 안에 stock이 있으니 바로 접근)
        Stock stock = product.getStock();
        
        if (stock == null) {
            throw new IllegalStateException("해당 상품의 재고 정보가 존재하지 않습니다.");
        }

        // 3. 재고 강제 업데이트 (Dirty Checking으로 반영됨)
        stock.setQuantity(newQuantity); 

        String redisKey = "stock:product:" + productId;
        redisTemplate.opsForValue().set(redisKey, String.valueOf(newQuantity));

    }

    public Product getProductById(Long productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));
    }
}
