package com.osy.commerce.user.api;

import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import com.osy.commerce.user.dto.ChangePasswordRequest;
import com.osy.commerce.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<ApiResponse> me(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return Responses.error(ApiCode.AUTH_REQUIRED, null);
        }
        Long userId = (Long) auth.getPrincipal();
        return Responses.ok(userService.getProfile(userId));
    }

    @PatchMapping("/users/me/password")
    public ResponseEntity<ApiResponse> changePassword(Authentication auth,
                                                      @Valid @RequestBody ChangePasswordRequest req) {
        if (auth == null || auth.getPrincipal() == null) {
            return Responses.error(ApiCode.AUTH_REQUIRED, null);
        }
        Long userId = (Long) auth.getPrincipal();
        userService.changePassword(userId, req.getCurrentPassword(), req.getNewPassword());
        return Responses.ok("OK");
    }
}
