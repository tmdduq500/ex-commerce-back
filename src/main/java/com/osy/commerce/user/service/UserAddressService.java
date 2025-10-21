package com.osy.commerce.user.service;

import com.osy.commerce.global.error.ApiException;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.domain.UserAddress;
import com.osy.commerce.user.dto.UserAddressCreate;
import com.osy.commerce.user.dto.UserAddressResponse;
import com.osy.commerce.user.dto.UserAddressUpdate;
import com.osy.commerce.user.repository.UserAddressRepository;
import com.osy.commerce.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserAddressService {

    private final UserRepository userRepository;
    private final UserAddressRepository addressRepository;

    @Transactional
    public List<UserAddressResponse> getUserAddressList(Long userId) {
        return addressRepository.findAllByUserIdOrderByIsDefaultDescIdDesc(userId)
                .stream().map(UserAddressResponse::from).toList();
    }

    @Transactional
    public UserAddressResponse createUserAddress(Long userId, UserAddressCreate req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiCode.USER_NOT_FOUND, "사용자 없음"));

        boolean setDefault = Boolean.TRUE.equals(req.getIsDefault())
                || addressRepository.countByUserIdAndIsDefaultTrue(userId) == 0;

        if (setDefault) {
            addressRepository.clearDefaultByUserId(userId);
        }

        UserAddress a = UserAddress.builder()
                .user(user)
                .recipient(req.getReceiverName())
                .phone(req.getReceiverPhone())
                .zipcode(req.getPostalCode())
                .address1(req.getAddress1())
                .address2(req.getAddress2())
                .isDefault(setDefault)
                .build();

        addressRepository.save(a);
        return UserAddressResponse.from(a);
    }

    @Transactional
    public UserAddressResponse modifyUserAddress(Long userId, Long addressId, UserAddressUpdate req) {
        UserAddress a = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주소를 찾을 수 없습니다."));

        if (req.getReceiverName() != null) a.updateRecipient(req.getReceiverName());
        if (req.getReceiverPhone() != null) a.updatePhone(req.getReceiverPhone());
        if (req.getPostalCode() != null) a.updateZipcode(req.getPostalCode());
        if (req.getAddress1() != null) a.updateAddress1(req.getAddress1());
        if (req.getAddress2() != null) a.updateAddress2(req.getAddress2());

        if (req.getIsDefault() != null) {
            if (req.getIsDefault()) {
                addressRepository.clearDefaultByUserId(userId);
                a.updateIsDefault(true);
            } else {
                a.updateIsDefault(false);
            }
        }

        return UserAddressResponse.from(a);
    }

    @Transactional
    public void deleteUserAddress(Long userId, Long addressId) {
        UserAddress a = addressRepository.findByIdAndUserId(addressId, userId)
                .orElseThrow(() -> new ApiException(ApiCode.NOT_FOUND, "주소를 찾을 수 없습니다."));

        boolean wasDefault = a.getIsDefault();
        addressRepository.delete(a);

        if (wasDefault) {
            var remain = addressRepository.findAllByUserIdOrderByIsDefaultDescIdDesc(userId);
            if (!remain.isEmpty()) {
                addressRepository.clearDefaultByUserId(userId);
                remain.get(0).updateIsDefault(true);
            }
        }
    }
}
