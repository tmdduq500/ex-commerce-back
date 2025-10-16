package com.osy.commerce.review.service;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.review.domain.Review;
import com.osy.commerce.review.domain.ReviewLike;
import com.osy.commerce.review.domain.ReviewLikeId;
import com.osy.commerce.review.repository.ReviewLikeRepository;
import com.osy.commerce.review.repository.ReviewRepository;
import com.osy.commerce.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ReviewLikeRepository reviewLikeRepository;

    @Transactional
    public Review addReview(User user, Product product, int rating, String title, String content) {
        boolean exists = reviewRepository.existsByUserAndProduct(user, product);
        if (exists) {
            throw new IllegalStateException("이미 이 상품에 대한 리뷰를 작성하셨습니다.");
        }
        Review review = Review.of(user, product, rating, title, content);
        return reviewRepository.save(review);
    }

    @Transactional
    public void editReview(Long reviewId, User user, int newRating, String newTitle, String newContent) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인 리뷰만 수정할 수 있습니다.");
        }
        review.edit(newRating, newTitle, newContent);
    }

    @Transactional
    public void deleteReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getId().equals(user.getId())) {
            throw new SecurityException("본인 리뷰만 삭제할 수 있습니다.");
        }
        reviewRepository.delete(review);
    }

    public List<Review> getReviewsByProduct(Product product) {
        return reviewRepository.findAllByProduct(product);
    }

    @Transactional
    public void likeReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));


        ReviewLikeId reviewLikeId = new ReviewLikeId(user.getId(), reviewId);
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
    public void unlikeReview(Long reviewId, User user) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));


        ReviewLikeId reviewLikeId = new ReviewLikeId(user.getId(), reviewId);
        if (!reviewLikeRepository.existsById(reviewLikeId.getReviewId())) {
            throw new IllegalStateException("좋아요 상태가 아닙니다.");
        }
        reviewLikeRepository.deleteById(reviewLikeId.getReviewId());
        review.decreaseLikeCount();
    }
}