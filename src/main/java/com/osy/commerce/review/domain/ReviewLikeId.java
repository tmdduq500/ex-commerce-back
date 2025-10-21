package com.osy.commerce.review.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ReviewLikeId implements Serializable {

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "review_id")
    private Long reviewId;
}
