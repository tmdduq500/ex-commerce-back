package com.osy.commerce.payment.service;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.global.error.ApiException;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.inventory.domain.StockTxn;
import com.osy.commerce.inventory.repository.StockTxnRepository;
import com.osy.commerce.order.domain.OrderItem;
import com.osy.commerce.order.domain.OrderStatus;
import com.osy.commerce.order.domain.Orders;
import com.osy.commerce.order.repository.OrderItemRepository;
import com.osy.commerce.order.repository.OrdersRepository;
import com.osy.commerce.payment.domain.Payment;
import com.osy.commerce.payment.domain.PaymentStatus;
import com.osy.commerce.payment.dto.PaymentCancelResponse;
import com.osy.commerce.payment.dto.PaymentConfirmRequest;
import com.osy.commerce.payment.dto.PaymentConfirmResponse;
import com.osy.commerce.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PaymentService {
    private final OrdersRepository ordersRepository;
    private final ProductRepository productRepository;
    private final PaymentRepository paymentRepository;
    private final StockTxnRepository stockTxnRepository;
    private final StringRedisTemplate redisTemplate;
    private final OrderItemRepository orderItemRepository;

    @Transactional
    public PaymentConfirmResponse confirmPayment(Long userId, PaymentConfirmRequest req, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ApiException(ApiCode.VALIDATION_ERROR, "Idempotency-Key 헤더가 필요합니다.");
        }
        final String idemKey = "PAYMENT_CONFIRM:" + req.getOrderId() + ":" + idempotencyKey;

        // 멱등 락 (중복 실행 방지)
        Boolean locked = redisTemplate.opsForValue().setIfAbsent(idemKey, "LOCK", Duration.ofMinutes(5));
        if (Boolean.FALSE.equals(locked)) {
            throw new ApiException(ApiCode.CONFLICT, "중복 결제 요청입니다.");
        }

        try {
            Orders order = ordersRepository.findByIdAndUserId(req.getOrderId(), userId)
                    .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주문을 찾을 수 없습니다."));
            if (order.getStatus() != OrderStatus.PENDING) {
                throw new ApiException(ApiCode.CONFLICT, "결제할 수 없는 주문 상태입니다.");
            }

            // 결제 조회
            Payment payment = paymentRepository.findByOrderId(order.getId()).orElse(null);
            if (payment != null && payment.getStatus() == PaymentStatus.PAID) {
                return PaymentConfirmResponse.from(order, payment);
            }
            if (payment == null) {
                payment = Payment.builder()
                        .order(order)
                        .status(PaymentStatus.PENDING)
                        .method(req.getMethod())
                        .provider(req.getProvider())
                        .merchantUid(order.getOrderNumber())
                        .amount(order.getTotalAmount()) // total_amount = 최종 결제금액
                        .build();
            }

            // 주문 아이템 조회
            List<OrderItem> items = orderItemRepository.findAllByOrderIdOrderByIdAsc(order.getId());

            items.sort(Comparator.comparing(oi -> oi.getProduct().getId()));

            // 재고 차감
            for (OrderItem item : items) {
                Long productId = item.getProduct().getId();

                Product product = productRepository.findWithPessimisticLockById(productId)
                        .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "상품이 존재하지 않습니다: " + productId));

                int remain = product.getStock();
                int need   = item.getQuantity();
                if (remain < need) {
                    throw new ApiException(ApiCode.CONFLICT, "재고 부족: " + productId);
                }

                product.setStock(remain - need);
                productRepository.save(product);

                stockTxnRepository.save(StockTxn.builder()
                        .product(product)
                        .orderItem(item)
                        .delta(-need)
                        .reason("ORDER")
                        .memo("Order #" + order.getOrderNumber())
                        .createdAt(LocalDateTime.now())
                        .build());
            }

            // 결제 확정 및 주문 상태 변경
            payment.setStatus(PaymentStatus.PAID);
            if (payment.getPgTid() == null) {
                payment.setPgTid("PG-" + System.currentTimeMillis());
            }
            payment.setApprovedAt(LocalDateTime.now());
            payment.setFailedReason(null);
            paymentRepository.save(payment);

            order.setStatus(OrderStatus.PAID);
            ordersRepository.save(order);

            return PaymentConfirmResponse.from(order, payment);

        } finally {
            redisTemplate.opsForValue().set(idemKey, "DONE", Duration.ofMinutes(30));
        }
    }

    @Transactional
    public PaymentCancelResponse cancelPayment(Long userId, Long orderId, String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.isBlank()) {
            throw new ApiException(ApiCode.VALIDATION_ERROR, "Idempotency-Key 헤더가 필요합니다.");
        }
        String idemKey = "PAYMENT_CANCEL:" + orderId + ":" + idempotencyKey;

        Boolean lock = redisTemplate.opsForValue().setIfAbsent(idemKey, "LOCK", Duration.ofMinutes(5));
        if (Boolean.FALSE.equals(lock)) {
            throw new ApiException(ApiCode.CONFLICT, "중복 취소 요청입니다.");
        }

        try {
            Orders order = ordersRepository.findByIdAndUserId(orderId, userId)
                    .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주문을 찾을 수 없습니다."));

            Payment p = paymentRepository.findByOrderId(orderId)
                    .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "결제 정보를 찾을 수 없습니다."));

            if (order.getStatus() == OrderStatus.CANCELLED || order.getStatus() == OrderStatus.REFUNDED
                    || p.getStatus() == PaymentStatus.CANCELLED || p.getStatus() == PaymentStatus.REFUNDED) {
                return PaymentCancelResponse.from(order, p);
            }

            // 취소 가능 상태 확인
            if (!(order.getStatus() == OrderStatus.PAID || order.getStatus() == OrderStatus.PREPARING)) {
                throw new ApiException(ApiCode.CONFLICT, "해당 주문 상태에서는 취소할 수 없습니다.");
            }

            List<OrderItem> orderItems = orderItemRepository.findByOrder(order)
                    .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주문을 찾을 수 없습니다."));

            // 재고 복구
            for (OrderItem item : orderItems) {
                Product product = item.getProduct();
                int restored = product.getStock() + item.getQuantity();
                product.setStock(restored);
                productRepository.save(product);

                stockTxnRepository.save(StockTxn.builder()
                        .product(product)
                        .orderItem(item)
                        .delta(+item.getQuantity())
                        .reason("CANCEL")
                        .memo("Order #" + order.getOrderNumber())
                        .createdAt(LocalDateTime.now())
                        .build());
            }

            // 결제 상태/주문 상태 변경
            p.setStatus(PaymentStatus.CANCELLED);
            p.setFailedReason(null);
            p.setApprovedAt(p.getApprovedAt());
            paymentRepository.save(p);

            order.setStatus(OrderStatus.CANCELLED);
            ordersRepository.save(order);

            return PaymentCancelResponse.from(order, p);

        } finally {
            redisTemplate.opsForValue().set(idemKey, "DONE", Duration.ofMinutes(30));
        }
    }

}
