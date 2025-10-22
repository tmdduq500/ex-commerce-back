package com.osy.commerce.cart.repository;

import com.osy.commerce.cart.domain.Cart;
import com.osy.commerce.cart.domain.CartItem;
import com.osy.commerce.catalog.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    List<CartItem> findAllByCart(Cart cart);
    void deleteByCartAndProduct(Cart cart, Product product);
}
