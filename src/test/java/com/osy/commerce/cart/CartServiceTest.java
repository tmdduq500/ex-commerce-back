package com.osy.commerce.cart;

import com.osy.commerce.cart.domain.Cart;
import com.osy.commerce.cart.domain.CartItem;
import com.osy.commerce.cart.repository.CartItemRepository;
import com.osy.commerce.cart.repository.CartRepository;
import com.osy.commerce.cart.service.CartService;
import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

public class CartServiceTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CartService cartService;

    private User user;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).build();
        product = Product.builder().id(10L).name("상품").price(1000).build();
        cart = Cart.builder().id(100L).user(user).build();
    }

    @Test
    @DisplayName("상품을 처음 장바구니에 담으면 새 CartItem 생성")
    void addItem_firstTime_createsNewCartItem() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.empty());

        cartService.addItem(1L, 10L, 2);

        verify(cartItemRepository).save(any(CartItem.class));
    }

    @Test
    @DisplayName("이미 담은 상품이면 수량 증가")
    void addItem_duplicate_increaseQuantity() {
        CartItem existing = CartItem.builder().cart(cart).product(product).quantity(1).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(existing));

        cartService.addItem(1L, 10L, 2);

        assertThat(existing.getQuantity()).isEqualTo(3);
        verify(cartItemRepository).save(existing);
    }

    @Test
    @DisplayName("수량 수정 성공")
    void updateItem_success() {
        CartItem item = CartItem.builder().cart(cart).product(product).quantity(1).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartItemRepository.findByCartAndProduct(cart, product)).thenReturn(Optional.of(item));

        cartService.updateItem(1L, 10L, 5);

        assertThat(item.getQuantity()).isEqualTo(5);
        verify(cartItemRepository).save(item);
    }

    @Test
    @DisplayName("장바구니에서 상품 제거")
    void removeItem_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        cartService.removeItem(1L, 10L);

        verify(cartItemRepository).deleteByCartAndProduct(cart, product);
    }

    @Test
    @DisplayName("장바구니 전체 조회")
    void getItems_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));

        cartService.getItems(1L);

        verify(cartItemRepository).findAllByCart(cart);
    }
}
