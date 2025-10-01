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
public class PaymentCancelResponse {
    private Long orderId;
    private Long paymentId;
    private String orderStatus;    
    private String paymentStatus;
    private Integer refundAmount;
    private LocalDateTime cancelledAt;

    public static PaymentCancelResponse from(Orders o, Payment p) {
        return PaymentCancelResponse.builder()
                .orderId(o.getId())
                .paymentId(p.getId())
                .orderStatus(o.getStatus().name())
                .paymentStatus(p.getStatus().name())
                .refundAmount(p.getAmount())
                .cancelledAt(p.getUpdatedAt() != null ? p.getUpdatedAt() : p.getApprovedAt())
                .build();
    }
}
