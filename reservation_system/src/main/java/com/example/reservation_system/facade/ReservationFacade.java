package com.example.reservation_system.facade;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

import com.example.reservation_system.ReservationCacheManager;
import com.example.reservation_system.service.ReservationService;

import org.springframework.kafka.core.KafkaTemplate;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReservationFacade {
    private final ReservationService innerService; 
    private final ReservationCacheManager cacheManager;
    private final RedissonClient redissonClient;


    private final KafkaTemplate<String, String> kafkaTemplate;
    

    public Long createReservation(Long userId, Long productId, int amount, String type) {
    
        if ("queued-async".equals(type)){
            boolean isDecremented = cacheManager.decrementStock(productId, amount);
            
            if(!isDecremented) throw new RuntimeException("재고가 부족합니다.");

            String message = userId + ":" + productId + ":" + amount;
            kafkaTemplate.send("reservation-topic", message); // 메시지가 쌓이는 채널 이름 
            return -1L;
        }

        if ("optimistic".equals(type)){ 
            return createWithOptimisticRetry(userId, productId, amount, type);
        }
        
        if ("distributed".equals(type)) {
            return createWithDistributedLock(userId, productId, amount, type);
        }

        return innerService.createReservation(userId, productId, amount, type);
    }

    private Long createWithDistributedLock(Long userId, Long productId, int amount, String type) {
        RLock lock = redissonClient.getFairLock("LOCK:" + productId);

        try {
            boolean available = lock.tryLock(25, 10, TimeUnit.SECONDS);
            if (!available) {
                throw new RuntimeException( "대기 시간이 초과되었습니다. 다시 시도해주세요.");
            }
            return innerService.createReservation(userId, productId, amount, type);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        } finally {
            if (lock.isHeldByCurrentThread()) {
                lock.unlock();
            }
        }
    }



    /**
     * 낙관적 락 재시도 로직 
     * @param user
     * @param productId
     * @param amount
     * @param type
     * @return
     */
    private Long createWithOptimisticRetry(Long userId, Long productId, int amount, String type) {
    int maxRetry = 500; // 최대 50번만 시도
    int retryCount = 0;
        
    while (retryCount < maxRetry) {
        try {
            return innerService.createReservation(userId, productId, amount, type);
        } catch (ObjectOptimisticLockingFailureException e) {
            retryCount++;
            log.info("낙관적 락 충돌 발생 ({}회차) - productId: {}", retryCount, productId);
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(ie);
            }
        } catch (Exception e) { // 낙관적 락 외의 예외가 터지면 바로 던져서 루프 탈출!
            log.error("예상치 못한 에러 발생: {}", e.getMessage());
            throw e;
        }
    }
    throw new RuntimeException("최대 재시도 횟수를 초과했습니다. 낙관적 락 해결 불가.");
}
}