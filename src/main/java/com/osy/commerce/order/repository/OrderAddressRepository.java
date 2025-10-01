package com.osy.commerce.order.repository;

import com.osy.commerce.order.domain.OrderAddress;
import com.osy.commerce.order.domain.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {
    Optional<OrderAddress> findByOrder(Orders order);

    List<OrderAddress> findAllByOrderIdIn(List<Long> orderIds);
}
