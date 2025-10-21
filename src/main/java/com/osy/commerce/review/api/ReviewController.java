package com.osy.commerce.review.api;

import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import com.osy.commerce.review.dto.*;
import com.osy.commerce.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse> createReview(@AuthenticationPrincipal Long userId,
                                                    @RequestBody ReviewCreateRequest request) {
        request.setUserId(userId);
        return Responses.created(reviewService.createReview(request));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteReview(@AuthenticationPrincipal Long userId,
                                                    @RequestBody ReviewDeleteRequest request) {
        request.setUserId(userId);
        reviewService.deleteReview(request);
        return Responses.ok();
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> modifyReview(@PathVariable Long reviewId,
                                                    @AuthenticationPrincipal Long userId,
                                                    @RequestBody ReviewModifyRequest request) {
        request.setUserId(userId);
        request.setReviewId(reviewId);
        reviewService.modifyReview(request);
        return Responses.ok();
    }

    @PostMapping("/like")
    public ResponseEntity<ApiResponse> likeReview(@AuthenticationPrincipal Long userId,
                                                  @RequestBody ReviewLikeRequest request) {
        request.setUserId(userId);
        reviewService.likeReview(request);
        return Responses.ok();
    }

    @DeleteMapping("/like")
    public ResponseEntity<ApiResponse> unlikeReview(@AuthenticationPrincipal Long userId,
                                                    @RequestBody ReviewLikeRequest request) {
        request.setUserId(userId);
        reviewService.unlikeReview(request);
        return Responses.ok();
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse> getReviewsByProduct(@PathVariable Long productId,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getReviewsByProduct(productId, pageable);
        return Responses.ok(reviews);
    }
}
