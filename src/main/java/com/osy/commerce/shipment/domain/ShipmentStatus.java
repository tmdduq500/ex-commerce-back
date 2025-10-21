package com.osy.commerce.shipment.domain;

public enum ShipmentStatus {
    READY, // 출고 준비
    SHIPPED, // 배송 시작
    DELIVERED, // 배송 완료
    RETURNED, // 반품
    CANCELLED // 취소
}
