package com.example.reservation_system.service;

import com.example.reservation_system.entity.Product;
import com.example.reservation_system.entity.Stock;
import com.example.reservation_system.entity.User;
import com.example.reservation_system.facade.ReservationFacade;
import com.example.reservation_system.repository.ProductRepository;
import com.example.reservation_system.repository.ReservationRepository;
import com.example.reservation_system.repository.StockRepository;
import com.example.reservation_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // ✅ 중요: 테스트 완료 후 모든 변경사항을 Rollback하여 DB를 깨끗하게 유지합니다.
public class SingleThreadTest {

    @Autowired private ReservationFacade reservationFacade;
    @Autowired private ReservationRepository reservationRepository;
    @Autowired private StockRepository stockRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ProductRepository productRepository;

    private Long productId;
    private Long userId;

    @BeforeEach
    void setUp() {
        // @Transactional이 있으므로 deleteAll()을 굳이 하지 않아도 되지만, 
        // 테스트 간의 독립성을 위해 필요한 데이터만 생성합니다.
        
        // 1. 유저 생성
        User user = User.builder()
                .loginId("concurUser")
                .password("password123!")
                .email("test@example.com")
                .phoneNumber("010-1234-5678")
                .build();
        userId = userRepository.save(user).getId();

        // 2. 상품 생성
        Product product = Product.builder()
                .name("단일요청상품")
                .price(1000)
                .build();
        Product savedProduct = productRepository.save(product);
        productId = savedProduct.getId();

        // 3. 재고 생성 (상품과 연결)
        Stock stock = Stock.builder()
                .product(savedProduct)
                .quantity(10)
                .build();
        stockRepository.save(stock);

        System.out.println("=== Setup Complete: UserID=" + userId + ", ProductID=" + productId + " ===");
    }

    @Test
    @DisplayName("단일 요청 성공 테스트: 10개 중 1개 예약 시 9개 잔여")
    void singleThreadTest() {
        // when
        reservationFacade.createReservation(userId, productId, 1, "optimistic");

        // then
        Stock stock = stockRepository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("재고 없음"));
        
        assertEquals(9, stock.getQuantity(), "재고가 정확히 9개여야 합니다.");
        
        // 예약 데이터가 실제로 생성됐는지도 확인
        long count = reservationRepository.count();
        assertTrue(count > 0, "예약 데이터가 생성되어야 합니다.");
    }
}