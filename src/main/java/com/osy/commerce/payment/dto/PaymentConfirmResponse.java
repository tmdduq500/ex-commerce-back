package com.osy.commerce.payment.dto;

import com.osy.commerce.order.domain.Orders;
import com.osy.commerce.payment.domain.Payment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PaymentConfirmResponse {
    private Long orderId;
    private Long paymentId;
    private String orderStatus;
    private String paymentStatus;
    private LocalDateTime paidAt;

    public static PaymentConfirmResponse from(Orders order, Payment payment) {
        return PaymentConfirmResponse.builder()
                .orderId(order.getId())
                .paymentId(payment.getId())
                .orderStatus(order.getStatus().name())
                .paymentStatus(payment.getStatus().name())
                .paidAt(payment.getApprovedAt())
                .build();
    }
}