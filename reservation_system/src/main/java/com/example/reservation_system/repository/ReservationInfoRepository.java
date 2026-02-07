package com.example.reservation_system.repository;

import com.example.reservation_system.entity.ReservationInfo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;



public interface ReservationInfoRepository extends JpaRepository<ReservationInfo, Long> {
    List<ReservationInfo> findByProductId(Long productId);
}
