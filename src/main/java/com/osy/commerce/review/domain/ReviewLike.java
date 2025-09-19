package com.osy.commerce.review.domain;

import com.osy.commerce.user.domain.User;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "review_like")
public class ReviewLike {
    @EmbeddedId
    private ReviewLikeId id;

    @MapsId("userId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id",
            foreignKey = @ForeignKey(name = "fk_review_like_user"))
    private User user;

    @MapsId("reviewId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id",
            foreignKey = @ForeignKey(name = "fk_review_like_review"))
    private Review review;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
class ReviewLikeId implements Serializable {
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "review_id")
    private Long reviewId;
}

