package com.osy.commerce.cart.dto;

import com.osy.commerce.cart.domain.CartItem;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CartItemResponse {
    private Long productId;
    private String productName;
    private int quantity;
    private int price;


    public static CartItemResponse from(CartItem item) {
        CartItemResponse res = new CartItemResponse();
        res.setProductId(item.getProduct().getId());
        res.setProductName(item.getProduct().getName());
        res.setQuantity(item.getQuantity());
        res.setPrice(item.getProduct().getPrice());
        return res;
    }
}
