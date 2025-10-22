package com.osy.commerce.cart.repository;

import com.osy.commerce.cart.domain.Cart;
import com.osy.commerce.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}
