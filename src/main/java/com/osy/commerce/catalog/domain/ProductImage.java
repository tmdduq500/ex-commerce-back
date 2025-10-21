package com.osy.commerce.catalog.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "product_image",
        indexes = @Index(name = "idx_product_image_prod", columnList = "product_id,sort_order"))
public class ProductImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_image_product"))
    private Product product;

    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    @Column(name = "is_primary", nullable = false)
    private Boolean isPrimary;

    @Column(name = "sort_order", nullable = false)
    private Integer sortOrder;
}
