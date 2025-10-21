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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ApiResponse> createReview(@RequestBody ReviewCreateRequest request) {
        return Responses.created(reviewService.createReview(request));
    }

    @DeleteMapping
    public ResponseEntity<ApiResponse> deleteReview(@RequestBody ReviewDeleteRequest request) {
        reviewService.deleteReview(request);
        return Responses.ok();
    }

    @PutMapping
    public ResponseEntity<ApiResponse> modifyReview(@RequestBody ReviewModifyRequest request) {
        reviewService.modifyReview(request);
        return Responses.ok();
    }

    @PostMapping("/like")
    public ResponseEntity<ApiResponse> likeReview(@RequestBody ReviewLikeRequest request) {
        reviewService.likeReview(request);
        return Responses.ok();
    }

    @DeleteMapping("/like")
    public ResponseEntity<ApiResponse> unlikeReview(@RequestBody ReviewLikeRequest request) {
        reviewService.unlikeReview(request);
        return Responses.ok();
    }

    @GetMapping("/products/{productId}")
    public ResponseEntity<ApiResponse> getReviewsByProduct(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<ReviewResponse> reviews = reviewService.getReviewsByProduct(productId, pageable);
        return Responses.ok(reviews);
    }
}
