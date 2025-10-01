package com.osy.commerce.order.dto;

import com.osy.commerce.order.domain.OrderAddress;
import com.osy.commerce.order.domain.OrderItem;
import com.osy.commerce.order.domain.Orders;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponse {
    private Long id;
    private String orderNumber;
    private String status;
    private Integer totalAmount;
    private LocalDateTime orderedAt;

    private Address address;
    private List<Item> items;

    public static OrderResponse from(Orders o, OrderAddress oa, List<OrderItem> orderItems) {
        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .status(o.getStatus() != null ? o.getStatus().name() : null)
                .totalAmount(o.getTotalAmount())
                .orderedAt(o.getOrderedAt())
                .address(Address.from(oa))
                .items(orderItems == null ? Collections.emptyList()
                        : orderItems.stream().map(Item::from).toList())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Address {
        private String recipient;
        private String phone;
        private String zipcode;
        private String address1;
        private String address2;

        public static Address from(OrderAddress oa) {
            if (oa == null) return null;
            return Address.builder()
                    .recipient(oa.getRecipient())
                    .phone(oa.getPhone())
                    .zipcode(oa.getZipcode())
                    .address1(oa.getAddress1())
                    .address2(oa.getAddress2())
                    .build();
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Item {
        private Long productId;
        private String name;
        private Integer unitPrice;
        private Integer qty;
        private Integer lineTotal;

        public static Item from(OrderItem oi) {
            return Item.builder()
                    .productId(oi.getProduct().getId())
                    .name(oi.getProductName())
                    .unitPrice(oi.getUnitPrice())
                    .qty(oi.getQuantity())
                    .lineTotal(oi.getLineAmount())
                    .build();
        }
    }
}
