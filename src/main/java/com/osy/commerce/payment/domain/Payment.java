package com.osy.commerce.payment.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import com.osy.commerce.order.domain.Orders;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment",
        indexes = {
                @Index(name = "idx_payment_order", columnList = "order_id"),
                @Index(name = "idx_payment_status", columnList = "status")
        })
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false, unique = true)
    private Orders order;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private PaymentStatus status;

    @Column(length = 32)
    private String method;

    @Column(length = 64)
    private String provider;

    @Column(name = "merchant_uid", length = 100)
    private String merchantUid;

    @Column(name = "pg_tid", length = 100)
    private String pgTid;

    @Column(nullable = false)
    private Integer amount;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "failed_reason", length = 500)
    private String failedReason;

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    public void updateFailedReason(String failedReason) {
        this.failedReason = failedReason;
    }

    public void updateApprovedAt(LocalDateTime time) {
        this.approvedAt = time;
    }

    public void updatePgTid(String pgTid) {
        this.pgTid = pgTid;
    }
}
