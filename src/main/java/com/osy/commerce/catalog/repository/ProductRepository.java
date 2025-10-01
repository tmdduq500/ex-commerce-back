package com.osy.commerce.catalog.repository;

import com.osy.commerce.catalog.domain.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    // 재고 차감 시에만 사용하는 비관락 메서드
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Product> findWithPessimisticLockById(Long id);
}
