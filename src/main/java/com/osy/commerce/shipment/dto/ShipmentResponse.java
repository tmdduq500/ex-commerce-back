package com.osy.commerce.shipment.dto;

import com.osy.commerce.shipment.domain.Shipment;
import com.osy.commerce.shipment.domain.ShipmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ShipmentResponse {
    private Long id;
    private Long orderId;
    private ShipmentStatus status;
    private String carrier;
    private String trackingNo;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;

    public static ShipmentResponse from(Shipment shipment) {
        return ShipmentResponse.builder()
                .id(shipment.getId())
                .orderId(shipment.getOrder().getId())
                .status(shipment.getStatus())
                .carrier(shipment.getCarrier())
                .trackingNo(shipment.getTrackingNo())
                .shippedAt(shipment.getShippedAt())
                .deliveredAt(shipment.getDeliveredAt())
                .build();
    }
}