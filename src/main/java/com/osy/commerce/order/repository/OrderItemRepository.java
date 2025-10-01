package com.osy.commerce.order.repository;

import com.osy.commerce.order.domain.OrderItem;
import com.osy.commerce.order.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    Optional<List<OrderItem>> findByOrder(Orders order);

    List<OrderItem> findAllByOrderIdIn(List<Long> orderIds);

    List<OrderItem> findAllByOrderIdOrderByIdAsc(Long id);
}
