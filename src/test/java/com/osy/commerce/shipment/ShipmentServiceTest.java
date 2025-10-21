package com.osy.commerce.shipment;

import com.osy.commerce.order.domain.OrderStatus;
import com.osy.commerce.order.domain.Orders;
import com.osy.commerce.order.repository.OrdersRepository;
import com.osy.commerce.shipment.domain.Shipment;
import com.osy.commerce.shipment.domain.ShipmentStatus;
import com.osy.commerce.shipment.dto.ShipmentResponse;
import com.osy.commerce.shipment.dto.ShipmentStartRequest;
import com.osy.commerce.shipment.repository.ShipmentRepository;
import com.osy.commerce.shipment.service.ShipmentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class ShipmentServiceTest {

    @Mock
    private ShipmentRepository shipmentRepository;
    @Mock
    private OrdersRepository ordersRepository;

    @InjectMocks
    private ShipmentService shipmentService;

    private Orders orders;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        orders = Orders.builder().id(1L).status(OrderStatus.PAID).build();
    }

    @Test
    @DisplayName("배송 시작 성공")
    void startShipment_success() {
        ShipmentStartRequest req = new ShipmentStartRequest();
        req.setCarrier("CJ대한통운");
        req.setTrackingNo("1234567890");

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(orders));

        shipmentService.startShipment(1L, req);

        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }

    @Test
    @DisplayName("배송 조회 성공")
    void getShipment_success() {
        Shipment shipment = Shipment.builder()
                .id(100L)
                .order(orders)
                .status(ShipmentStatus.SHIPPED)
                .carrier("CJ")
                .trackingNo("T123")
                .build();

        when(shipmentRepository.findByOrderId(1L)).thenReturn(Optional.of(shipment));

        ShipmentResponse response = shipmentService.getShipment(1L);

        assertThat(response.getOrderId()).isEqualTo(1L);
        assertThat(response.getCarrier()).isEqualTo("CJ");
    }

    @Test
    @DisplayName("배송 완료 처리")
    void completeShipment_success() {
        Shipment shipment = Shipment.builder()
                .id(100L)
                .order(orders)
                .status(ShipmentStatus.SHIPPED)
                .build();

        when(shipmentRepository.findByOrderId(1L)).thenReturn(Optional.of(shipment));

        shipmentService.completeShipment(1L);

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
    }

    @Test
    @DisplayName("배송 조회 실패: 데이터 없음")
    void getShipment_notFound() {
        when(shipmentRepository.findByOrderId(99L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> shipmentService.getShipment(99L));
    }

    @Test
    @DisplayName("배송 시작 시 주문 상태도 SHIPPED로 변경")
    void startShipment_shouldUpdateOrderStatus() {
        ShipmentStartRequest req = new ShipmentStartRequest();
        req.setCarrier("CJ대한통운");
        req.setTrackingNo("1234567890");


        when(ordersRepository.findById(1L)).thenReturn(Optional.of(orders));
        when(shipmentRepository.findByOrderId(1L)).thenReturn(Optional.empty());

        shipmentService.startShipment(1L, req);

        assertThat(orders.getStatus()).isEqualTo(OrderStatus.SHIPPED);
        verify(shipmentRepository, times(1)).save(any(Shipment.class));
    }


    @Test
    @DisplayName("배송 완료 시 주문 상태도 DELIVERED로 변경")
    void completeShipment_shouldUpdateOrderStatus() {
        Shipment shipment = Shipment.builder()
                .id(100L)
                .order(orders)
                .status(ShipmentStatus.SHIPPED)
                .build();


        when(shipmentRepository.findByOrderId(1L)).thenReturn(Optional.of(shipment));

        shipmentService.completeShipment(1L);

        assertThat(shipment.getStatus()).isEqualTo(ShipmentStatus.DELIVERED);
        assertThat(orders.getStatus()).isEqualTo(OrderStatus.DELIVERED);
    }


    @Test
    @DisplayName("배송 시작 실패: 이미 배송된 주문")
    void startShipment_shouldFailIfDuplicate() {
        Shipment existing = Shipment.builder().id(2L).order(orders).build();

        when(ordersRepository.findById(1L)).thenReturn(Optional.of(orders));
        when(shipmentRepository.findByOrderId(1L)).thenReturn(Optional.of(existing));

        ShipmentStartRequest req = new ShipmentStartRequest();
        req.setCarrier("CJ");
        req.setTrackingNo("T0000");

        assertThrows(IllegalStateException.class, () -> shipmentService.startShipment(1L, req));
    }
}
