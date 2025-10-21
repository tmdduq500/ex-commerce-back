package com.osy.commerce.shipment.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class ShipmentStartRequest {
    private String carrier;
    private String trackingNo;
}