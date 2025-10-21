package com.osy.commerce.review.service;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.review.domain.Review;
import com.osy.commerce.review.domain.ReviewLike;
import com.osy.commerce.review.domain.ReviewLikeId;
import com.osy.commerce.review.dto.*;
import com.osy.commerce.review.repository.ReviewLikeRepository;
import com.osy.commerce.review.repository.ReviewRepository;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    @Transactional
    public Long createReview(ReviewCreateRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        boolean exists = reviewRepository.existsByUserAndProduct(user, product);
        if (exists) {
            throw new IllegalStateException("이미 이 상품에 대한 리뷰를 작성하셨습니다.");
        }
        Review review = Review.of(user, product, request.getRating(), request.getTitle(), request.getContent());
        return reviewRepository.save(review).getId();
    }

    @Transactional
    public void modifyReview(ReviewModifyRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인 리뷰만 수정할 수 있습니다.");
        }
        review.edit(request.getRating(), request.getTitle(), request.getContent());
    }

    @Transactional
    public void deleteReview(ReviewDeleteRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인 리뷰만 삭제할 수 있습니다.");
        }
        reviewRepository.delete(review);
    }

    public Page<ReviewResponse> getReviewsByProduct(Long productId, Pageable pageable) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));
        return reviewRepository.findAllByProduct(product, pageable)
                .map(ReviewResponse::from);
    }

    @Transactional
    public void likeReview(ReviewLikeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        ReviewLikeId reviewLikeId = new ReviewLikeId(user.getId(), review.getId());
        if (reviewLikeRepository.existsById(reviewLikeId.getReviewId())) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
        }
        ReviewLike like = ReviewLike.builder()
                .id(reviewLikeId)
                .user(user)
                .review(review)
                .createdAt(LocalDateTime.now())
                .build();
        reviewLikeRepository.save(like);
        review.increaseLikeCount();
    }

    @Transactional
    public void unlikeReview(ReviewLikeRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        Review review = reviewRepository.findById(request.getReviewId())
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));

        ReviewLikeId reviewLikeId = new ReviewLikeId(user.getId(), review.getId());
        if (!reviewLikeRepository.existsById(reviewLikeId.getReviewId())) {
            throw new IllegalStateException("좋아요 상태가 아닙니다.");
        }
        reviewLikeRepository.deleteById(reviewLikeId.getReviewId());
        review.decreaseLikeCount();
    }
}
