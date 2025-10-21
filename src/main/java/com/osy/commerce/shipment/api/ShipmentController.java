package com.osy.commerce.shipment.api;

import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import com.osy.commerce.shipment.dto.ShipmentResponse;
import com.osy.commerce.shipment.dto.ShipmentStartRequest;
import com.osy.commerce.shipment.service.ShipmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipments")
@RequiredArgsConstructor
public class ShipmentController {

    private final ShipmentService shipmentService;

    @PostMapping("/{orderId}/start")
    public ResponseEntity<ApiResponse> startShipment(@PathVariable Long orderId, @RequestBody ShipmentStartRequest request) {
        shipmentService.startShipment(orderId, request);
        return Responses.ok();
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<ApiResponse> getShipment(@PathVariable Long orderId) {
        ShipmentResponse response = shipmentService.getShipment(orderId);
        return Responses.ok(response);
    }

    @PostMapping("/{orderId}/complete")
    public ResponseEntity<ApiResponse> completeShipment(@PathVariable Long orderId) {
        shipmentService.completeShipment(orderId);
        return Responses.ok();
    }
}
