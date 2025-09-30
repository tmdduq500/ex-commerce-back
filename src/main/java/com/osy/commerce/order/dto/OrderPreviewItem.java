package com.osy.commerce.order.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderPreviewItem {
    private Long productId;
    private String name;
    private int unitPrice;
    private int qty;
    private int lineTotal;
}
