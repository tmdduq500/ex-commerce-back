// ReviewServiceTest.java
package com.osy.commerce.review;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.review.domain.Review;
import com.osy.commerce.review.dto.ReviewCreateRequest;
import com.osy.commerce.review.dto.ReviewDeleteRequest;
import com.osy.commerce.review.dto.ReviewModifyRequest;
import com.osy.commerce.review.dto.ReviewResponse;
import com.osy.commerce.review.repository.ReviewLikeRepository;
import com.osy.commerce.review.repository.ReviewRepository;
import com.osy.commerce.review.service.ReviewService;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ReviewServiceTest {

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

    private User user;
    private Product product;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).build();
        product = Product.builder().id(10L).build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
    }

    @Test
    @DisplayName("리뷰 최초 등록 성공")
    void testAddReviewSuccess() {
        String content = "좋은 상품입니다";
        int rating = 5;

        when(reviewRepository.existsByUserAndProduct(user, product)).thenReturn(false);

        Review savedReview = Review.of(user, product, rating, "제목", content);
        savedReview.setId(999L);

        when(reviewRepository.save(any())).thenReturn(savedReview);
        when(reviewRepository.findById(999L)).thenReturn(Optional.of(savedReview));

        Long createdReview = reviewService.createReview(
                new ReviewCreateRequest(user.getId(), product.getId(), rating, "제목", content)
        );

        Review result = reviewRepository.findById(createdReview).orElse(null);

        assertThat(result).isNotNull();
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getProduct()).isEqualTo(product);
        assertThat(result.getContent()).isEqualTo(content);
        assertThat(result.getRating()).isEqualTo(rating);
    }

    @Test
    @DisplayName("동일 상품에 중복 리뷰 등록 시도시 예외 발생")
    void testAddReviewDuplicate() {
        when(reviewRepository.existsByUserAndProduct(user, product)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> reviewService.createReview(new ReviewCreateRequest(user.getId(), product.getId(), 4, "제목", "내용")));
    }

    @Test
    @DisplayName("리뷰 수정 성공")
    void testEditReviewSuccess() {
        Review review = Review.of(user, product, 3, "이전 제목", "이전 내용");
        review = Review.builder().id(100L).user(user).product(product).title("이전 제목").content("이전 내용").rating(3).likeCount(0).build();

        when(reviewRepository.findById(100L)).thenReturn(Optional.of(review));

        reviewService.modifyReview(new ReviewModifyRequest(review.getId(), user.getId(), null, 5, "수정된 제목", "수정된 내용"));

        assertThat(review.getTitle()).isEqualTo("수정된 제목");
        assertThat(review.getContent()).isEqualTo("수정된 내용");
        assertThat(review.getRating()).isEqualTo(5);
    }

    @Test
    @DisplayName("리뷰 삭제 성공")
    void testDeleteReviewSuccess() {
        Review review = Review.builder().id(101L).user(user).product(product).title("삭제 대상").content("내용").rating(4).likeCount(0).build();

        when(reviewRepository.findById(101L)).thenReturn(Optional.of(review));

        reviewService.deleteReview(new ReviewDeleteRequest(101L, user.getId()));

        verify(reviewRepository, times(1)).delete(review);
    }

    @Test
    @DisplayName("상품별 리뷰 목록 조회")
    void testListProductReviews() {
        Review r1 = Review.of(user, product, 5, "제목1", "내용1");
        Review r2 = Review.of(user, product, 4, "제목2", "내용2");
        Pageable pageable = PageRequest.of(0, 10);

        Page<Review> reviewPage = new PageImpl<>(Arrays.asList(r1, r2), pageable, 2);

        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(reviewRepository.findAllByProduct(product, pageable)).thenReturn(reviewPage);

        Page<ReviewResponse> result = reviewService.getReviewsByProduct(product.getId(), pageable);

        assertThat(result).hasSize(2);
        assertThat(result).extracting("title").contains("제목1", "제목2");
    }
}
