package com.osy.commerce.order.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import com.osy.commerce.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "orders",
        uniqueConstraints = @UniqueConstraint(name = "uk_orders_order_number", columnNames = "order_number"),
        indexes = {
                @Index(name = "idx_orders_user", columnList = "user_id"),
                @Index(name = "idx_orders_status", columnList = "status")
        })
public class Orders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_number", nullable = false, length = 50)
    private String orderNumber;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE RESTRICT
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_orders_users"))
    private User user;

    @Column(nullable = false, length = 32)
    private OrderStatus status;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 500)
    private String note;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
}
