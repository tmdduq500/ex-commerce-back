package com.osy.commerce.coupon.service;

import com.osy.commerce.coupon.domain.Coupon;
import com.osy.commerce.coupon.domain.CouponRedemption;
import com.osy.commerce.coupon.domain.CouponStatus;
import com.osy.commerce.coupon.domain.DiscountType;
import com.osy.commerce.coupon.repository.CouponRedemptionRepository;
import com.osy.commerce.coupon.repository.CouponRepository;
import com.osy.commerce.user.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final CouponRepository couponRepository;
    private final CouponRedemptionRepository redemptionRepository;

    public List<CouponRedemption> getAvailableCoupons(User user) {
        return redemptionRepository.findByUserAndStatus(user, CouponStatus.ISSUED);
    }

    public CouponRedemption validateCouponForOrder(User user, String couponCode, int orderAmount) {
        Coupon coupon = couponRepository.findByCode(couponCode)
                .orElseThrow(() -> new IllegalArgumentException("쿠폰이 존재하지 않습니다."));

        CouponRedemption redemption = redemptionRepository.findByUserAndCoupon(user, coupon)
                .orElseThrow(() -> new IllegalStateException("해당 쿠폰을 보유하고 있지 않습니다."));

        if (redemption.getStatus() != CouponStatus.ISSUED) {
            throw new IllegalStateException("이미 사용된 쿠폰입니다.");
        }

        LocalDateTime now = LocalDateTime.now();
        if (coupon.getStartsAt() != null && now.isBefore(coupon.getStartsAt())) {
            throw new IllegalStateException("아직 사용 불가능한 쿠폰입니다.");
        }
        if (coupon.getEndsAt() != null && now.isAfter(coupon.getEndsAt())) {
            throw new IllegalStateException("기간이 만료된 쿠폰입니다.");
        }
        if (coupon.getMinOrderAmt() != null && orderAmount < coupon.getMinOrderAmt()) {
            throw new IllegalStateException("최소 주문 금액을 충족하지 못했습니다.");
        }


        return redemption;
    }

    @Transactional
    public void redeemCoupon(CouponRedemption redemption) {
        redemption.redeem();
    }

    public int calculateDiscount(Coupon coupon, int orderAmount) {
        if (coupon.getDiscountType() == null) return 0;

        int discount = 0;

        switch (coupon.getDiscountType()) {
            case AMOUNT:
                discount = coupon.getDiscountValue();
                break;
            case RATE:
                discount = (int) Math.floor(orderAmount * (coupon.getDiscountValue() / 100.0));
                if (coupon.getMaxDiscount() != null) {
                    discount = Math.min(discount, coupon.getMaxDiscount());
                }
                break;
        }
        return discount;
    }

}
