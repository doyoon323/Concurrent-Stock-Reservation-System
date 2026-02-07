package com.example.reservation_system.repository;

import com.example.reservation_system.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;



public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsByName(String name);
    boolean existsById(Long productId);    
}
