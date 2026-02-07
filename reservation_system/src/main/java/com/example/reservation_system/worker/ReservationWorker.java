package com.example.reservation_system.worker;

import com.example.reservation_system.entity.*; // 레포지토리와 엔티티가 있는 패키지 경로 (중요!)
import com.example.reservation_system.repository.*; // 만약 레포지토리가 별도 폴더면 추가
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.support.Acknowledgment; // 이 임포트가 반드시 있어야 합니다.

import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReservationWorker {
    private final ReservationRepository reservationRepository;
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @KafkaListener(topics = "reservation-topic", groupId= "reservation-group-v1")
    @Transactional
    public void processReservation(String message, Acknowledgment ack){

        try {
            String[] parts = message.split(":");
            if (parts.length != 3) {
                log.error("잘못된 메시지 형식: {}", message);
                return; // 포맷 오류는 재시도하지 않음
            }


            Long userId = Long.parseLong(parts[0]);
            Long productId = Long.parseLong(parts[1]); // 수정 완료
            int amount = Integer.parseInt(parts[2]);    // 수정 완료

            // 비즈니스 로직
            Stock stock = stockRepository.findByProductIdWithPessimisticLock(productId)
                .orElseThrow(() -> new RuntimeException("존재하는 상품이 아닙니다."));
            
            stock.decreaseQuantity(amount);

            User user = userRepository.getReferenceById(userId);
            Product product = productRepository.getReferenceById(productId);

            Reservation reservation = Reservation.builder()
                .user(user)
                .product(product)
                .amount(amount)
                .status("COMPLETED")
                .expiryDate(LocalDateTime.now().plusDays(1))
                .build();

            reservationRepository.save(reservation);

            ack.acknowledge();
            
        } catch (Exception e) {
            log.error("주문 처리 중 에러 발생! 메시지: {}, 에러: {}", message, e.getMessage());
            // 실무 Tip: 여기서 RuntimeException을 던지면 카프카가 재시도를 시도합니다.
            // 그냥 catch만 하면 성공으로 간주하니 주의!
            throw new RuntimeException("Rollback for Kafka retry"); 
        }
    }
}