package com.osy.commerce.user.domain;

import com.osy.commerce.global.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "user_address",
        indexes = {
                @Index(name = "idx_user_address_user", columnList = "user_id"),
                @Index(name = "idx_user_address_default", columnList = "user_id,is_default")
        })
public class UserAddress extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) // ON DELETE CASCADE
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_user_address_user"))
    private User user;

    @Column(nullable = false, length = 100)
    private String recipient;

    @Column(length = 30)
    private String phone;

    @Column(length = 20)
    private String zipcode;

    @Column(name = "address1", nullable = false, length = 255)
    private String address1;

    @Column(name = "address2", length = 255)
    private String address2;

    @Column(length = 100)
    private String city;

    @Column(length = 100)
    private String state;

    @Column(length = 2)
    private String country;

    @Column(name = "is_default", nullable = false)
    private Boolean isDefault;
}
