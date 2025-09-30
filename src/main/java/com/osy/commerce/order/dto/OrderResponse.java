package com.osy.commerce.order.dto;

import com.osy.commerce.order.domain.OrderAddress;
import com.osy.commerce.order.domain.OrderItem;
import com.osy.commerce.order.domain.Orders;
import lombok.*;

import java.time.LocalDateTime;
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
    private int totalAmount;
    private int payableAmount;
    private LocalDateTime createdAt;
    private Address address;
    private List<Item> items;

    public static OrderResponse from(Orders o) {
        return OrderResponse.builder()
                .id(o.getId())
                .orderNumber(o.getOrderNumber())
                .status(o.getStatus())
                .totalAmount(o.getTotalAmount())
                .payableAmount(o.getTotalAmount())
                .createdAt(o.getCreatedAt())
                .address(Address.from(o.getOrderAddress())) // ← 1:1 스냅샷 사용
                .items(o.getItems().stream().map(Item::from).toList())
                .build();
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Item {
        private Long productId;
        private String name;
        private int unitPrice;
        private int qty;
        private int lineTotal;

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

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Address {
        private String receiverName;
        private String receiverPhone;
        private String postalCode;
        private String address1;
        private String address2;

        public static Address from(OrderAddress oa) {
            return Address.builder()
                    .receiverName(oa.getRecipient())
                    .receiverPhone(oa.getPhone())
                    .postalCode(oa.getZipcode())
                    .address1(oa.getAddress1())
                    .address2(oa.getAddress2())
                    .build();
        }
    }
}
