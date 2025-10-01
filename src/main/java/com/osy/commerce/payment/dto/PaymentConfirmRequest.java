package com.osy.commerce.payment.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentConfirmRequest {
    private Long orderId;
    private String method;
    private String provider;
}