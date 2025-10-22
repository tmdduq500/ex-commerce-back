package com.osy.commerce.cart.service;

import com.osy.commerce.cart.domain.Cart;
import com.osy.commerce.cart.domain.CartItem;
import com.osy.commerce.cart.repository.CartItemRepository;
import com.osy.commerce.cart.repository.CartRepository;
import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    private Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> cartRepository.save(Cart.builder().user(user).build()));
    }

    @Transactional
    public void addItem(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .map(ci -> {
                    ci.changeQuantity(ci.getQuantity() + quantity);
                    return ci;
                })
                .orElse(CartItem.builder()
                        .cart(cart)
                        .product(product)
                        .quantity(quantity)
                        .build());

        cartItemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long userId, Long productId, int quantity) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        Cart cart = getOrCreateCart(user);

        CartItem item = cartItemRepository.findByCartAndProduct(cart, product)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 없습니다."));

        item.changeQuantity(quantity);
        cartItemRepository.save(item);
    }

    @Transactional
    public void removeItem(Long userId, Long productId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        Cart cart = getOrCreateCart(user);

        cartItemRepository.deleteByCartAndProduct(cart, product);
    }

    public List<CartItem> getItems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Cart cart = getOrCreateCart(user);
        return cartItemRepository.findAllByCart(cart);
    }
}
