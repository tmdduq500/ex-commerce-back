package com.osy.commerce.review;


import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.review.domain.Review;
import com.osy.commerce.review.domain.ReviewLike;
import com.osy.commerce.review.domain.ReviewLikeId;
import com.osy.commerce.review.dto.ReviewLikeRequest;
import com.osy.commerce.review.repository.ReviewLikeRepository;
import com.osy.commerce.review.repository.ReviewRepository;
import com.osy.commerce.review.service.ReviewService;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


public class ReviewLikeTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private ReviewLikeRepository reviewLikeRepository;

    @InjectMocks
    private ReviewService reviewService;

    private User liker;
    private Product product;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        liker = User.builder().id(7L).build();
        product = Product.builder().id(77L).build();
        review = Review.builder()
                .id(777L)
                .user(User.builder().id(1L).build())
                .product(product)
                .title("제목")
                .content("내용")
                .rating(5)
                .likeCount(0)
                .build();
        when(userRepository.findById(liker.getId())).thenReturn(Optional.of(liker));
    }

    @Test
    @DisplayName("리뷰 좋아요 성공: 처음 누르면 likeCount=1")
    void likeReview_success_firstTime() {
        when(reviewRepository.findById(777L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.existsById(new ReviewLikeId(7L, 777L).getReviewId())).thenReturn(false);

        reviewService.likeReview(new ReviewLikeRequest(777L, liker.getId()));

        assertThat(review.getLikeCount()).isEqualTo(1);

        ArgumentCaptor<ReviewLike> captor = ArgumentCaptor.forClass(ReviewLike.class);
        verify(reviewLikeRepository, times(1)).save(captor.capture());
        ReviewLike saved = captor.getValue();
        assertThat(saved.getId().getUserId()).isEqualTo(7L);
        assertThat(saved.getId().getReviewId()).isEqualTo(777L);
    }

    @Test
    @DisplayName("리뷰 좋아요 중복 방지: 이미 누른 경우 예외")
    void likeReview_duplicate_shouldThrow() {
        when(reviewRepository.findById(777L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.existsById(new ReviewLikeId(7L, 777L).getReviewId())).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> reviewService.likeReview(new ReviewLikeRequest(777L, liker.getId())));
        assertThat(review.getLikeCount()).isEqualTo(0);
        verify(reviewLikeRepository, never()).save(any());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소 성공: 기존 좋아요가 있으면 취소하고 likeCount 감소")
    void unlikeReview_success() {
        review.setLikeCount(1);
        when(reviewRepository.findById(777L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.existsById(new ReviewLikeId(7L, 777L).getReviewId())).thenReturn(true);

        reviewService.unlikeReview(new ReviewLikeRequest(777L, liker.getId()));

        assertThat(review.getLikeCount()).isEqualTo(0);
        verify(reviewLikeRepository, times(1)).deleteById(new ReviewLikeId(7L, 777L).getReviewId());
    }

    @Test
    @DisplayName("리뷰 좋아요 취소: 좋아요 안한 상태에서 취소 시 예외")
    void unlikeReview_withoutLike_shouldThrow() {
        when(reviewRepository.findById(777L)).thenReturn(Optional.of(review));
        when(reviewLikeRepository.existsById(new ReviewLikeId(7L, 777L).getReviewId())).thenReturn(false);

        assertThrows(IllegalStateException.class, () -> reviewService.unlikeReview(new ReviewLikeRequest(777L, liker.getId())));
        assertThat(review.getLikeCount()).isEqualTo(0);
        verify(reviewLikeRepository, never()).deleteById(any());
    }
}