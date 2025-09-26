package com.osy.commerce.user.api;

import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import com.osy.commerce.user.dto.UserAddressCreate;
import com.osy.commerce.user.dto.UserAddressUpdate;
import com.osy.commerce.user.service.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me/addresses")
public class UserAddressController {

    private final UserAddressService userAddressService;

    private Long currentUserId(Authentication auth) {
        return (Long) auth.getPrincipal();
    }

    @GetMapping
    public ResponseEntity<ApiResponse> getUserAddressList(Authentication auth) {
        return Responses.ok(userAddressService.getUserAddressList(currentUserId(auth)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse> createUserAddress(Authentication auth, @Valid @RequestBody UserAddressCreate req) {
        return Responses.created(userAddressService.createUserAddress(currentUserId(auth), req));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse> modifyUserAddress(Authentication auth, @PathVariable Long id,
                                    @Valid @RequestBody UserAddressUpdate req) {
        return Responses.ok(userAddressService.modifyUserAddress(currentUserId(auth), id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUserAddress(Authentication auth, @PathVariable Long id) {
        userAddressService.deleteUserAddress(currentUserId(auth), id);
        return Responses.ok();
    }
}
