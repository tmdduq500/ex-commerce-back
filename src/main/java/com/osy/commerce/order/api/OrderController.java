package com.osy.commerce.order.api;

import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.PageResponse;
import com.osy.commerce.global.response.Responses;
import com.osy.commerce.global.security.CustomUserDetails;
import com.osy.commerce.order.domain.OrderStatus;
import com.osy.commerce.order.dto.CreateOrderRequest;
import com.osy.commerce.order.dto.OrderListResponse;
import com.osy.commerce.order.dto.OrderPreviewRequest;
import com.osy.commerce.order.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @PostMapping("/preview")
    public ResponseEntity<?> preview(Authentication auth, @Valid @RequestBody OrderPreviewRequest req) {
        return Responses.ok(orderService.preview(currentUserId(auth), req));
    }

    @PostMapping
    public ResponseEntity<?> createOrder(Authentication auth, @Valid @RequestBody CreateOrderRequest req) {
        return Responses.created(orderService.createOrder(currentUserId(auth), req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(Authentication auth, @PathVariable Long id) {
        return Responses.ok(orderService.getOrder(currentUserId(auth), id));
    }

    @GetMapping
    public ResponseEntity<?> getOrderList(Authentication auth) {
        return Responses.ok(orderService.getOrderList(currentUserId(auth)));
    }

    @GetMapping("/orders/my")
    public ResponseEntity<ApiResponse> getMyOrders(
            Authentication auth,
            @RequestParam(required = false) OrderStatus status,
            @PageableDefault(size = 10) Pageable pageable) {

        Page<OrderListResponse> page = orderService.getMyOrders(currentUserId(auth), status, pageable);
        return Responses.ok(PageResponse.from(page));
    }

}
