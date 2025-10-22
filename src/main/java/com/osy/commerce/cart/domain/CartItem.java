package com.osy.commerce.cart.domain;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_item",
        uniqueConstraints = @UniqueConstraint(columnNames = {"cart_id", "product_id"}))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class CartItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id", nullable = false)
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int quantity;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public void changeQuantity(int newQuantity) {
        this.quantity = newQuantity;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

}
