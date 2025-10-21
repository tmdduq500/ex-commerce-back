package com.osy.commerce.shipment.service;

import com.osy.commerce.order.domain.Orders;
import com.osy.commerce.order.repository.OrdersRepository;
import com.osy.commerce.shipment.domain.Shipment;
import com.osy.commerce.shipment.domain.ShipmentStatus;
import com.osy.commerce.shipment.dto.ShipmentResponse;
import com.osy.commerce.shipment.dto.ShipmentStartRequest;
import com.osy.commerce.shipment.repository.ShipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final OrdersRepository ordersRepository;

    @Transactional
    public void startShipment(Long orderId, ShipmentStartRequest request) {
        Orders orders = ordersRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

        Shipment shipment = Shipment.builder()
                .order(orders)
                .status(ShipmentStatus.SHIPPED)
                .carrier(request.getCarrier())
                .trackingNo(request.getTrackingNo())
                .shippedAt(LocalDateTime.now())
                .build();


        shipmentRepository.save(shipment);
    }

    public ShipmentResponse getShipment(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
        return ShipmentResponse.from(shipment);
    }

    @Transactional
    public void completeShipment(Long orderId) {
        Shipment shipment = shipmentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setDeliveredAt(LocalDateTime.now());
    }
}
