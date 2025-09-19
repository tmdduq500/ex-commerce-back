package com.osy.commerce.inventory.domain;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.order.domain.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "stock_txn",
        indexes = @Index(name = "idx_stock_txn_product", columnList = "product_id"))
public class StockTxn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_stock_txn_product"))
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE SET NULL
    @JoinColumn(name = "order_item_id",
            foreignKey = @ForeignKey(name = "fk_stock_txn_order_item"))
    private OrderItem orderItem;

    @Column(nullable = false)
    private Integer delta;

    @Column(nullable = false, length = 32)
    private String reason;

    @Column(length = 255)
    private String memo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
