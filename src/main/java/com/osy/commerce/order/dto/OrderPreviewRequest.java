package com.osy.commerce.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OrderPreviewRequest {
    @NotNull
    private List<Item> items;

    @NotNull
    private Long addressId;

    private String couponCode;

    @Getter
    @Setter
    public static class Item {
        @NotNull
        private Long productId;
        @Min(1)
        private int qty;
    }
}
