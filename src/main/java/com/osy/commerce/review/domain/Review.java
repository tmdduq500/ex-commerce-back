package com.osy.commerce.review.domain;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.global.jpa.BaseEntity;
import com.osy.commerce.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "review",
        uniqueConstraints = @UniqueConstraint(name = "uq_review_user_product",
                columnNames = {"user_id", "product_id"}),
        indexes = @Index(name = "idx_review_prod", columnList = "product_id"))
public class Review extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_users"))
    private User user;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "product_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_review_product"))
    private Product product;

    @Column(nullable = false) // TINYINT
    private Integer rating;

    @Column(length = 255)
    private String title;

    @Lob
    private String content; // TEXT

    @Column(name = "like_count", nullable = false)
    private Integer likeCount;

    public static Review of(User user, Product product, int rating, String title, String content) {
        return Review.builder()
                .user(user)
                .product(product)
                .rating(rating)
                .title(title)
                .content(content)
                .likeCount(0)
                .build();
    }

    public void edit(int rating, String title, String content) {
        this.rating = rating;
        this.title = title;
        this.content = content;
    }

    public void increaseLikeCount() {
        this.likeCount += 1;
    }

    public void decreaseLikeCount() {
        this.likeCount = Math.max(0, this.likeCount - 1);
    }
}

