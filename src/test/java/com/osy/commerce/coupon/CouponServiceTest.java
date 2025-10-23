package com.osy.commerce.coupon;

import com.osy.commerce.coupon.domain.Coupon;
import com.osy.commerce.coupon.domain.DiscountType;
import com.osy.commerce.coupon.service.CouponService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CouponServiceTest {

    private final CouponService couponService = new CouponService(null, null);

    @Test
    @DisplayName("정액 쿠폰 할인 적용 계산")
    void amountDiscount_shouldApplyCorrectly() {
        Coupon coupon = Coupon.builder()
                .discountType(DiscountType.AMOUNT)
                .discountValue(1000)
                .build();

        int discount = couponService.calculateDiscount(coupon, 5000);
        assertThat(discount).isEqualTo(1000);
    }

    @Test
    @DisplayName("정률 쿠폰 할인 적용 계산 (10%)")
    void rateDiscount_shouldApplyCorrectly() {
        Coupon coupon = Coupon.builder()
                .discountType(DiscountType.RATE)
                .discountValue(10) // 10%
                .build();

        int discount = couponService.calculateDiscount(coupon, 5000);
        assertThat(discount).isEqualTo(500);
    }

    @Test
    @DisplayName("정률 쿠폰에 최대 할인 제한이 있는 경우")
    void rateDiscount_shouldRespectMaxDiscount() {
        Coupon coupon = Coupon.builder()
                .discountType(DiscountType.RATE)
                .discountValue(50) // 50%
                .maxDiscount(2000)
                .build();

        int discount = couponService.calculateDiscount(coupon, 10000);
        assertThat(discount).isEqualTo(2000); // 원래 5000이지만 max 제한
    }

    @Test
    @DisplayName("정률 쿠폰 계산 시 소수점 절삭 확인")
    void rateDiscount_shouldRoundDown() {
        Coupon coupon = Coupon.builder()
                .discountType(DiscountType.RATE)
                .discountValue(15) // 15%
                .build();

        int discount = couponService.calculateDiscount(coupon, 3333); // 499.95
        assertThat(discount).isEqualTo(499); // floor 적용
    }
}
