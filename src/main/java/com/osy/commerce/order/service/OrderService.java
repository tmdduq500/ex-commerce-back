package com.osy.commerce.order.service;

import com.osy.commerce.catalog.domain.Product;
import com.osy.commerce.catalog.repository.ProductRepository;
import com.osy.commerce.global.error.ApiException;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.order.domain.OrderAddress;
import com.osy.commerce.order.domain.OrderItem;
import com.osy.commerce.order.domain.OrderStatus;
import com.osy.commerce.order.domain.Orders;
import com.osy.commerce.order.dto.*;
import com.osy.commerce.order.repository.OrderAddressRepository;
import com.osy.commerce.order.repository.OrderItemRepository;
import com.osy.commerce.order.repository.OrdersRepository;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.domain.UserAddress;
import com.osy.commerce.user.repository.UserAddressRepository;
import com.osy.commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {

    private final ProductRepository productRepository;
    private final UserAddressRepository userAddressRepository;

    private final OrdersRepository ordersRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderAddressRepository orderAddressRepository;
    private final UserRepository userRepository;

    @Transactional
    public OrderPreviewResponse preview(Long userId, OrderPreviewRequest req) {
        var items = req.getItems();
        if (items == null || items.isEmpty()) {
            throw new ApiException(ApiCode.VALIDATION_ERROR, "주문 품목이 비어있습니다.");
        }

        userAddressRepository.findByIdAndUserId(req.getAddressId(), userId)
                .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "배송지를 찾을 수 없습니다."));

        List<OrderPreviewItem> previewItems = new ArrayList<>();
        int subtotal = 0;

        for (var it : items) {
            Product p = productRepository.findById(it.getProductId())
                    .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "상품이 존재하지 않습니다: " + it.getProductId()));
            int unit = p.getPrice();
            int line = unit * it.getQty();

            previewItems.add(OrderPreviewItem.builder()
                    .productId(p.getId())
                    .name(p.getName())
                    .unitPrice(unit)
                    .qty(it.getQty())
                    .lineTotal(line)
                    .build());

            subtotal += line;
        }

        int shipping = calcShipping(subtotal);
        int discount = calcDiscount(req.getCouponCode(), subtotal);
        int payable = Math.max(0, subtotal + shipping - discount);

        return OrderPreviewResponse.builder()
                .items(previewItems)
                .summary(OrderPreviewResponse.Summary.builder()
                        .subtotal(subtotal)
                        .shipping(shipping)
                        .discount(discount)
                        .payable(payable)
                        .build())
                .build();
    }

    // ===== Create =====
    @Transactional
    public OrderResponse createOrder(Long userId, CreateOrderRequest req) {
        OrderPreviewResponse pv = preview(userId, toPreview(req));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Orders order = new Orders();
        order.setUser(user);
        order.setOrderNumber(generateOrderNumber());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(pv.getSummary().getPayable());
        order.setOrderedAt(LocalDateTime.now());
        ordersRepository.save(order);

        UserAddress ua = userAddressRepository.findByIdAndUserId(req.getAddressId(), userId)
                .orElseThrow(() -> new ApiException(ApiCode.USER_NOT_FOUND));

        OrderAddress orderAddress = OrderAddress.builder()
                .order(order)
                .recipient(ua.getRecipient())
                .phone(ua.getPhone())
                .zipcode(ua.getZipcode())
                .address1(ua.getAddress1())
                .address2(ua.getAddress2())
                .build();
        orderAddressRepository.save(orderAddress);

        // 아이템 생성 (단가/상품명 스냅샷)
        for (var it : pv.getItems()) {
            Product product = productRepository.findById(it.getProductId())
                    .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "상품을 찾을 수 없습니다."));
            OrderItem oi = new OrderItem();
            oi.setOrder(order);
            oi.setProduct(product);
            oi.setProductName(it.getName());
            oi.setUnitPrice(it.getUnitPrice());
            oi.setQuantity(it.getQty());
            oi.setLineAmount(it.getLineTotal());
            orderItemRepository.save(oi);
        }

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order)
                .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주문을 찾을 수 없습니다."));

        // 응답 DTO 변환
        return OrderResponse.from(order, orderAddress, orderItems);
    }

    @Transactional
    public OrderResponse getOrder(Long userId, Long orderId) {
        Orders order = ordersRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주문을 찾을 수 없습니다."));

        OrderAddress orderAddress = orderAddressRepository.findByOrder(order)
                .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주문의 주소를 찾을 수 없습니다."));

        List<OrderItem> orderItems = orderItemRepository.findByOrder(order)
                .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주문의 상세정보를 찾을 수 없습니다."));

        return OrderResponse.from(order, orderAddress, orderItems);
    }

    public List<OrderResponse> getOrderList(Long userId) {
        List<Orders> orders = ordersRepository.findAllByUserIdOrderByIdDesc(userId);
        List<Long> orderIds = orders.stream().map(Orders::getId).toList();

        // 주소들 미리 조회
        Map<Long, OrderAddress> addressMap = orderAddressRepository.findAllByOrderIdIn(orderIds)
                .stream().collect(Collectors.toMap(OrderAddress::getOrderId, oa -> oa));

        // 아이템들 미리 조회
        Map<Long, List<OrderItem>> itemsMap = orderItemRepository.findAllByOrderIdIn(orderIds)
                .stream().collect(Collectors.groupingBy(oi -> oi.getOrder().getId()));

        return orders.stream()
                .map(o -> OrderResponse.from(
                        o,
                        addressMap.get(o.getId()),
                        itemsMap.getOrDefault(o.getId(), List.of())
                ))
                .toList();
    }


    private OrderPreviewRequest toPreview(CreateOrderRequest req) {
        OrderPreviewRequest pv = new OrderPreviewRequest();
        pv.setItems(req.getItems());
        pv.setAddressId(req.getAddressId());
        pv.setCouponCode(req.getCouponCode());
        return pv;
    }

    private int calcShipping(int subtotal) {
        return subtotal >= 50000 ? 0 : 3000;
    }

    private int calcDiscount(String couponCode, int subtotal) {
        return 0; // todo 추후 쿠폰 붙일 때 구현
    }

    private String generateOrderNumber() {
        var now = java.time.LocalDate.now();
        int rnd = ThreadLocalRandom.current().nextInt(100000, 999999);
        String on = "%04d%02d%02d-%06d".formatted(now.getYear(), now.getMonthValue(), now.getDayOfMonth(), rnd);
        if (ordersRepository.existsByOrderNumber(on)) return generateOrderNumber();
        return on;
    }
}
