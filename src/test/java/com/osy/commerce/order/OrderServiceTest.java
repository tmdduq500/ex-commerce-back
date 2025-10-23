package com.osy.commerce.order;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.coupon.domain.Coupon;
import com.osy.commerce.coupon.domain.CouponRedemption;
import com.osy.commerce.coupon.domain.CouponStatus;
import com.osy.commerce.coupon.domain.DiscountType;
import com.osy.commerce.coupon.service.CouponService;
import com.osy.commerce.global.error.ApiException;
import com.osy.commerce.order.dto.CreateOrderRequest;
import com.osy.commerce.order.dto.OrderPreviewRequest;
import com.osy.commerce.order.dto.OrderResponse;
import com.osy.commerce.order.repository.OrderAddressRepository;
import com.osy.commerce.order.repository.OrderItemRepository;
import com.osy.commerce.order.repository.OrdersRepository;
import com.osy.commerce.order.service.OrderService;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.domain.UserAddress;
import com.osy.commerce.user.repository.UserAddressRepository;
import com.osy.commerce.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrdersRepository ordersRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CouponService couponService;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private OrderAddressRepository orderAddressRepository;
    @Mock
    private OrderItemRepository orderItemRepository;
    @Mock
    private UserAddressRepository userAddressRepository;

    @InjectMocks
    private OrderService orderService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = User.builder().id(1L).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        Product mockProduct = Product.builder()
                .id(1L)
                .name("테스트상품")
                .price(5000)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(mockProduct));
    }

    private OrderPreviewRequest.Item createSampleItem() {
        OrderPreviewRequest.Item item = new OrderPreviewRequest.Item();
        item.setProductId(1L);
        item.setQty(1);
        return item;
    }

    @Test
    @DisplayName("쿠폰 없이 주문 생성 성공")
    void createOrder_withoutCoupon_success() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setAddressId(1L);
        req.setItems(List.of(createSampleItem()));

        when(ordersRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userAddressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mock(UserAddress.class)));
        when(orderItemRepository.findByOrder(any())).thenReturn(Optional.of(List.of()));

        OrderResponse res = orderService.createOrder(1L, req);
        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("쿠폰 적용 주문 생성 성공")
    void createOrder_withCoupon_success() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setAddressId(1L);
        req.setItems(List.of(createSampleItem()));
        req.setCouponCode("SAVE10");

        Coupon coupon = Coupon.builder()
                .code("SAVE10")
                .discountType(DiscountType.AMOUNT)
                .discountValue(1000)
                .build();

        CouponRedemption redemption = CouponRedemption.builder()
                .coupon(coupon)
                .user(user)
                .status(CouponStatus.ISSUED)
                .build();

        when(couponService.validateCouponForOrder(eq(user), eq("SAVE10"), anyInt())).thenReturn(redemption);
        when(couponService.calculateDiscount(eq(coupon), anyInt())).thenReturn(1000);

        when(ordersRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(userAddressRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(mock(UserAddress.class)));
        when(orderItemRepository.findByOrder(any())).thenReturn(Optional.of(List.of()));

        OrderResponse res = orderService.createOrder(1L, req);
        assertThat(res).isNotNull();
    }

    @Test
    @DisplayName("만료되었거나 사용된 쿠폰 적용 시 예외 발생")
    void createOrder_withInvalidCoupon_shouldThrow() {
        CreateOrderRequest req = new CreateOrderRequest();
        req.setAddressId(1L);
        req.setItems(List.of(createSampleItem()));
        req.setCouponCode("EXPIRED");

        when(couponService.validateCouponForOrder(eq(user), eq("EXPIRED"), anyInt()))
                .thenThrow(new com.osy.commerce.global.error.ApiException(com.osy.commerce.global.response.ApiCode.VALIDATION_ERROR, "쿠폰이 유효하지 않습니다."));

        assertThrows(ApiException.class, () -> orderService.createOrder(1L, req));
    }
}
