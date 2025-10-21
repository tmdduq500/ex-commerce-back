package com.osy.commerce.coupon.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "coupon",
        uniqueConstraints = @UniqueConstraint(name = "uk_coupon_code", columnNames = "code"))
public class Coupon extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "discount_type", nullable = false, length = 16)
    private String discountType; // RATE|AMOUNT

    @Column(name = "discount_value", nullable = false)
    private Integer discountValue;

    @Column(name = "min_order_amt")
    private Integer minOrderAmt;

    @Column(name = "max_discount")
    private Integer maxDiscount;

    @Column(name = "starts_at")
    private LocalDateTime startsAt;

    @Column(name = "ends_at")
    private LocalDateTime endsAt;

    @Column(name = "issued_qty")
    private Integer issuedQty;
}
