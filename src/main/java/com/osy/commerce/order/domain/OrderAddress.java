package com.osy.commerce.order.domain;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "order_address")
public class OrderAddress {
    @Id
    @Column(name = "order_id")
    private Long orderId; // PK=FK

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId
    @JoinColumn(name = "order_id")
    private Orders order;

    @Column(nullable = false, length = 100)
    private String recipient;

    @Column(length = 30)
    private String phone;

    @Column(length = 20)
    private String zipcode;

    @Column(name = "address1", nullable = false)
    private String address1;

    @Column(name = "address2")
    private String address2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 2)
    private String country;
}
