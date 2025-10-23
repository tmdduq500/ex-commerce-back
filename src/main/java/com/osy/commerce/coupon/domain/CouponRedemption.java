package com.osy.commerce.coupon.domain;

import com.osy.commerce.order.domain.Orders;
import com.osy.commerce.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon_redemption",
        uniqueConstraints = @UniqueConstraint(name = "uq_coupon_user_once", columnNames = {"coupon_id", "user_id"}))
public class CouponRedemption {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "coupon_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_coupon_redemption_coupon"))
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_coupon_redemption_users"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE SET NULL
    @JoinColumn(name = "order_id",
            foreignKey = @ForeignKey(name = "fk_coupon_redemption_order"))
    private Orders order; // nullable

    @Column(name = "redeemed_at")
    private LocalDateTime redeemedAt;

    @Column(nullable = false, length = 16)
    private CouponStatus status; // ISSUED|REDEEMED|CANCELLED|EXPIRED

    public void redeem() {
        this.status = CouponStatus.REDEEMED;
        this.redeemedAt = LocalDateTime.now();
    }
}
