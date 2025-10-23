package com.osy.commerce.order.repository;

import com.osy.commerce.order.domain.OrderStatus;
import com.osy.commerce.order.domain.Orders;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {
    Optional<Orders> findByIdAndUserId(Long id, Long userId);

    List<Orders> findAllByUserIdOrderByIdDesc(Long userId);

    boolean existsByOrderNumber(String orderNumber);

    Page<Orders> findByUserIdAndStatus(Long userId, OrderStatus status, Pageable pageable);

    Page<Orders> findByUserId(Long userId, Pageable pageable);
}
