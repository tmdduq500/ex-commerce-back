package com.osy.commerce.order.dto;

import com.osy.commerce.order.domain.OrderItem;
import com.osy.commerce.order.domain.Orders;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class OrderListResponse {
    private Long orderId;
    private String orderNumber;
    private LocalDateTime orderedAt;
    private String status;
    private int totalAmount;
    private String productSummary;

    public static OrderListResponse from(Orders order, List<OrderItem> items) {
        String summary = items.isEmpty()
                ? "상품 없음"
                : items.get(0).getProductName() + (items.size() > 1 ? " 외 " + (items.size() - 1) + "건" : "");

        return OrderListResponse.builder()
                .orderId(order.getId())
                .orderNumber(order.getOrderNumber())
                .orderedAt(order.getOrderedAt())
                .status(order.getStatus().name())
                .totalAmount(order.getTotalAmount())
                .productSummary(summary)
                .build();
    }

}

