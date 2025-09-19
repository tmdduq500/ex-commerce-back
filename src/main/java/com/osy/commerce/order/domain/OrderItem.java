package com.osy.commerce.order.domain;

import com.osy.commerce.catalog.domain.Product;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_item",
        indexes = @Index(name = "idx_order_item_order", columnList = "order_id"))
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "order_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_order_item_order"))
    private Orders order;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE SET NULL
    @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(name = "fk_order_item_product"))
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "unit_price", nullable = false)
    private Integer unitPrice;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "line_amount", nullable = false)
    private Integer lineAmount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
