package com.example.reservation_system.service;

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

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
// @Transactional을 붙이지 않습니다. (각 쓰레드의 커밋이 DB에 실시간으로 반영되어야 하기 때문)
public class MultiThreadRequest {

    @Autowired
    private ReservationFacade reservationFacade;
    @Autowired
    private ProductService productService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ReservationRepository reservationRepository;

    private Long productId;
    private Long userId;

    @BeforeEach
    void setUp() {
        // 1. 역순 삭제 (FK 제약조건 방지)
        // 1. 가장 하위 자식인 예약(Reservation)부터 삭제
    reservationRepository.deleteAllInBatch();
    
    // 2. 상품을 참조하는 재고(Stock) 삭제
    stockRepository.deleteAllInBatch();
    
    // 3. 이제 아무도 참조하지 않는 상품(Product) 삭제
    productRepository.deleteAllInBatch();
    
    // 4. 유저(User) 삭제 (예약이 유저를 참조하므로 예약이 먼저 지워져야 함)
    userRepository.deleteAllInBatch();

        String uniqueSuffix = String.valueOf(System.currentTimeMillis());

        // 2. 유저 생성
        User user = User.builder()
                .loginId("user" + uniqueSuffix)
                .password("password123!")
                .email("test" + uniqueSuffix + "@example.com")
                .phoneNumber("010-0000-0000")
                .build();
        userId = userRepository.save(user).getId();

        // 3. 상품 및 재고 생성 (100개)
        productId = productService.createProduct("멀티상품" + uniqueSuffix, 2000, 200);
        
        System.out.println("=== 테스트 준비 완료: 상품ID=" + productId + " ===");
    }

    @Test
    @DisplayName("동시성 테스트: 200개의 재고에 대해 100명이 동시에 1개씩 예약 시도")
    void multiThreadReservation() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        // 성공/실패 카운팅을 위한 원자적 변수
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // when: 100명이 동시에 주문 시작
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                try {
                    // "optimistic" 타입을 넘겨서 파사드의 재시도 로직을 타게 함
                    reservationFacade.createReservation(userId, productId, 1, "optimistic");
                    successCount.incrementAndGet();
                } catch (Exception e) {
                    System.err.println("예약 실패 원인: " + e.getMessage());
                    failCount.incrementAndGet();
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 쓰레드가 종료될 때까지 대기
        executorService.shutdown();

        // then: 최종 재고 확인
        Stock stock = stockRepository.findByProductId(productId).orElseThrow();
        
        System.out.println("======================================");
        System.out.println("성공 횟수: " + successCount.get());
        System.out.println("실패 횟수: " + failCount.get());
        System.out.println("최종 남은 재고: " + stock.getQuantity());
        System.out.println("======================================");
        
        // 낙관적 락 재시도가 정상 작동했다면, 충돌을 뚫고 100개 모두 차감되어야 함
        assertEquals(100, stock.getQuantity(), "낙관적 락 재시도 결과 재고가 60이어야 합니다.");
    }
}