package com.osy.commerce.catalog.domain;

import com.osy.commerce.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "wishlist_item")
public class WishlistItem {
    @EmbeddedId
    private WishlistItemId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_wish_users"))
    private User user;

    @MapsId("productId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id",
            foreignKey = @ForeignKey(name = "fk_wish_product"))
    private Product product;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
class WishlistItemId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "product_id")
    private Long productId;
}
