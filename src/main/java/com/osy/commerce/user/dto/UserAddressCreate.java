package com.osy.commerce.user.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAddressCreate {

    @NotBlank
    private String receiverName;

    @NotBlank
    private String receiverPhone;

    @NotBlank
    private String postalCode;

    @NotBlank
    private String address1;
    private String address2;
    private Boolean isDefault = false;
}
