package com.osy.commerce.payment.api;

import com.osy.commerce.global.response.Responses;
import com.osy.commerce.payment.dto.PaymentConfirmRequest;
import com.osy.commerce.payment.service.PaymentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {
    private final PaymentService paymentService;

    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @PostMapping("/confirm")
    public ResponseEntity<?> confirmPayment(Authentication auth,
                                            @Valid @RequestBody PaymentConfirmRequest request,
                                            @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return Responses.ok(paymentService.confirmPayment(currentUserId(auth), request, idempotencyKey));
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<?> cancelPayment(Authentication auth,
                                    @PathVariable Long orderId,
                                    @RequestHeader("Idempotency-Key") String idempotencyKey) {
        return Responses.ok(paymentService.cancelPayment(currentUserId(auth), orderId, idempotencyKey));
    }

}
