package com.osy.commerce.review.repository;

import com.osy.commerce.review.domain.ReviewLike;
import com.osy.commerce.review.domain.ReviewLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewLikeRepository extends JpaRepository<ReviewLike, ReviewLikeId> {
}
