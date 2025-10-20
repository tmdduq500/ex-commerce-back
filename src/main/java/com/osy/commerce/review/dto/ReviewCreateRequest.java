package com.osy.commerce.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReviewCreateRequest {
    private Long userId;
    private Long productId;
    private int rating;
    private String title;
    private String content;
}
