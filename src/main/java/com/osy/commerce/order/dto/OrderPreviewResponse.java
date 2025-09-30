package com.osy.commerce.order.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPreviewResponse {
    private List<OrderPreviewItem> items;
    private Summary summary;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Summary {
        private int subtotal;
        private int shipping;
        private int discount;
        private int payable;
    }
}
