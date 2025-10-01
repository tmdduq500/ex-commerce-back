package com.osy.commerce.inventory.domain;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.order.domain.OrderItem;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_txn",
        indexes = @Index(name = "idx_stock_txn_product", columnList = "product_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockTxn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_item_id")
    private OrderItem orderItem;

    @Column(nullable = false)
    private Integer delta;       // 변동 수량 (-3 등)

    @Column(nullable = false, length = 32)
    private String reason;       // ORDER, RESTOCK 등

    @Column(length = 255)
    private String memo;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

