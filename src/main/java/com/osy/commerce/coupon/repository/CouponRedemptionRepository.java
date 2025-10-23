package com.osy.commerce.coupon.repository;

import com.osy.commerce.coupon.domain.Coupon;
import com.osy.commerce.coupon.domain.CouponRedemption;
import com.osy.commerce.coupon.domain.CouponStatus;
import com.osy.commerce.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CouponRedemptionRepository extends JpaRepository<CouponRedemption, Long> {
    Optional<CouponRedemption> findByUserAndCoupon(User user, Coupon coupon);

    List<CouponRedemption> findByUserAndStatus(User user, CouponStatus couponStatus);
}
