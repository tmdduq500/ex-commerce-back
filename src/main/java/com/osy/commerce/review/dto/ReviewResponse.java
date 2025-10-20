package com.osy.commerce.review.dto;

import com.osy.commerce.review.domain.Review;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponse {
    private Long id;
    private Long userId;
    private String userName;
    private int rating;
    private String title;
    private String content;
    private int likeCount;
    private LocalDateTime createdAt;

    public static ReviewResponse from(Review review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .userId(review.getUser().getId())
                .userName(review.getUser().getName())
                .rating(review.getRating())
                .title(review.getTitle())
                .content(review.getContent())
                .likeCount(review.getLikeCount())
                .createdAt(review.getCreatedAt())
                .build();
    }
}
