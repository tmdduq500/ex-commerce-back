package com.osy.commerce.order.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import com.osy.commerce.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
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
            foreignKey = @ForeignKey(name = "fk_orders_user"))
    private User user;

    @Column(nullable = false, length = 32)
    private String status;

    @Column(name = "total_amount", nullable = false)
    private Integer totalAmount;

    @Column(nullable = false, length = 3)
    private String currency;

    @Column(length = 500)
    private String note;

    @Column(name = "ordered_at", nullable = false)
    private LocalDateTime orderedAt;

    @OneToOne(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private OrderAddress orderAddress;

    @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    public void setOrderAddress(OrderAddress oa) {
        this.orderAddress = oa;
        if (oa != null && oa.getOrder() != this) oa.setOrder(this);
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
        if (item.getOrder() != this) item.setOrder(this);
    }

}
