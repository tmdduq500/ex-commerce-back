package com.osy.commerce.shipment.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import com.osy.commerce.order.domain.Orders;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "shipment",
        indexes = {
                @Index(name = "idx_shipment_order", columnList = "order_id"),
                @Index(name = "idx_shipment_tracking", columnList = "tracking_no")
        })
public class Shipment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_shipment_order"))
    private Orders order;

    @Column(nullable = false, length = 32)
    private ShipmentStatus status;

    @Column(length = 64)
    private String carrier;

    @Column(name = "tracking_no", length = 100)
    private String trackingNo;

    @Column(name = "shipped_at")
    private LocalDateTime shippedAt;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;
}
