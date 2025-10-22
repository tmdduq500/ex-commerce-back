package com.osy.commerce.cart.api;

import com.osy.commerce.cart.domain.CartItem;
import com.osy.commerce.cart.dto.CartItemRequest;
import com.osy.commerce.cart.dto.CartItemResponse;
import com.osy.commerce.cart.service.CartService;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/items")
    public ResponseEntity<ApiResponse> addItem(@RequestBody CartItemRequest request) {
        cartService.addItem(request.getUserId(), request.getProductId(), request.getQuantity());
        return Responses.ok();
    }

    @PutMapping("/items")
    public ResponseEntity<ApiResponse> updateItem(@RequestBody CartItemRequest request) {
        cartService.updateItem(request.getUserId(), request.getProductId(), request.getQuantity());
        return Responses.ok();
    }

    @DeleteMapping("/items")
    public ResponseEntity<ApiResponse> deleteItem(@RequestBody CartItemRequest request) {
        cartService.removeItem(request.getUserId(), request.getProductId());
        return Responses.ok();
    }

    @GetMapping("/items")
    public ResponseEntity<ApiResponse> getCartItems(@RequestParam Long userId) {
        List<CartItem> items = cartService.getItems(userId);
        List<CartItemResponse> response = items.stream()
                .map(CartItemResponse::from)
                .collect(Collectors.toList());
        return Responses.ok(response);
    }
}
