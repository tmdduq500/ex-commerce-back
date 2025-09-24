package com.osy.commerce.auth.api;

import com.osy.commerce.auth.dto.LoginRequest;
import com.osy.commerce.auth.dto.RefreshRequest;
import com.osy.commerce.auth.dto.SignupRequest;
import com.osy.commerce.auth.dto.TokenPairResponse;
import com.osy.commerce.auth.service.AuthService;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.security.jwt.JwtTokenProvider;
import com.osy.commerce.global.security.jwt.RefreshTokenStore;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenStore refreshTokenStore;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.signup(req)));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(req)));
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest req) {
        TokenPairResponse res = authService.refreshByToken(req.getRefreshToken());
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication auth) {
        if (auth == null || auth.getPrincipal() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("AUTH_ERROR", "인증 필요"));
        }
        Long userId = (Long) auth.getPrincipal();
        authService.logout(userId);
        return ResponseEntity.ok(ApiResponse.ok("OK"));
    }
}

