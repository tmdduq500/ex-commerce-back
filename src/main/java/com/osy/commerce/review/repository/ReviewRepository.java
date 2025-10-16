package com.osy.commerce.review.repository;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.review.domain.Review;
import com.osy.commerce.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByUserAndProduct(User user, Product product);
    List<Review> findAllByProduct(Product product);
}
