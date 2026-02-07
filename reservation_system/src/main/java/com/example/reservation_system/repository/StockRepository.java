package com.example.reservation_system.repository;

import com.example.reservation_system.entity.Stock;

import jakarta.persistence.LockModeType;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;



public interface StockRepository extends JpaRepository<Stock, Long> {
    // 동시성의 해결 - 비관적 락(Pessimistic Lock)
    /// 내가 재고를 수정하는 동안 아무도 이 재고 데이터에 손대지 마! 라고 DB에 자물쇠를 채우는 방식 
    /// 10,000명이 몰려와도 DB가 한명씩 순서대로 줄을 세워 처리하기 때문에 데이터가 꼬일 일이 없다
        // 아니 근데 이렇게 하면 ㅋㅋㅋ 부하가 너무 심하잖아? response time이 너무 안좋을듯

    /// 비관적 락 
    @Lock(LockModeType.PESSIMISTIC_WRITE) // for update 
    @Query("select s from Stock s where s.product.id= :productId")
    Optional<Stock> findByProductIdWithPessimisticLock(Long productId);


    /// 낙관적 락 
    /// 설마 동시에 수정하겠어? 같이 낙관적인 태도로 접근하는 방식
    /// DB에 자물쇠를 채우지 않는다. 대신 version을 보고 내가 읽었을 때랑 지금이라 똑같은지를 확인한다. 
    /// 실패했을 떄 다시 시도하는 로직 (WAS CPU 사용량 급증 현상 발생)
    /// "데이터를 수정하는 빈도가 낮고, 충돌 확률이 희박한 곳" 에 건다. 
    @Lock(LockModeType.OPTIMISTIC)
    @Query("select s from Stock s where s.product.id = :productId")
    Optional<Stock> findByProductIdWithOptimisticLock(Long productId);


    Optional<Stock> findByProductId(Long productId);
}