package com.osy.commerce.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserAddressUpdate {

    private String receiverName;
    private String receiverPhone;
    private String postalCode;
    private String address1;
    private String address2;
    private Boolean isDefault;
}
