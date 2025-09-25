package com.osy.commerce.auth.api;

import com.osy.commerce.auth.dto.LoginRequest;
import com.osy.commerce.auth.dto.RefreshRequest;
import com.osy.commerce.auth.dto.SignupRequest;
import com.osy.commerce.auth.service.AuthService;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest req) {
        return Responses.ok(authService.signup(req));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest req) {
        return Responses.ok(authService.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        return Responses.ok(authService.refreshByToken(req.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return Responses.error(ApiCode.AUTH_REQUIRED, null);
        }
        Long userId = (Long) auth.getPrincipal();
        authService.logout(userId);
        return Responses.ok("OK");
    }
}
