package com.osy.commerce.user.dto;

import com.osy.commerce.user.domain.UserAddress;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserAddressResponse {
    private Long id;
    private String receiverName;
    private String receiverPhone;
    private String postalCode;
    private String address1;
    private String address2;
    private boolean isDefault;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserAddressResponse from(UserAddress a) {
        return UserAddressResponse.builder()
                .id(a.getId())
                .receiverName(a.getRecipient())
                .receiverPhone(a.getPhone())
                .postalCode(a.getZipcode())
                .address1(a.getAddress1())
                .address2(a.getAddress2())
                .isDefault(a.getIsDefault())
                .createdAt(a.getCreatedAt())
                .updatedAt(a.getUpdatedAt())
                .build();
    }
}
