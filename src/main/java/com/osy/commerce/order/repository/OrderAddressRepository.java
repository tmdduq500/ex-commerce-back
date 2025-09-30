package com.osy.commerce.order.repository;

import com.osy.commerce.order.domain.OrderAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderAddressRepository extends JpaRepository<OrderAddress, Long> {
}
