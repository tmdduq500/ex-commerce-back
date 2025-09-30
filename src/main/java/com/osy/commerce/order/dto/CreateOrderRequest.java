package com.osy.commerce.order.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CreateOrderRequest {
    @NotNull
    private List<OrderPreviewRequest.Item> items;
    @NotNull
    private Long addressId;
    private String couponCode; // 선택
}
