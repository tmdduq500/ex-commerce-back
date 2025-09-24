package com.osy.commerce.global.api;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
public class PingController {

    // 보호 자원: 토큰 필요
    @GetMapping("/ping")
    public ResponseEntity<?> ping(Authentication auth) {
        // JwtAuthenticationFilter에서 principal로 userId(Long) 넣어놨다면:
        Object principal = (auth != null ? auth.getPrincipal() : null);
        return ResponseEntity.ok().body(
                java.util.Map.of("ok", true, "principal", Objects.requireNonNull(principal)));
    }

    @GetMapping("/api/me")
    public ResponseEntity<?> me(Authentication auth) {
        Object principal = (auth != null ? auth.getPrincipal() : null);
        return ResponseEntity.ok(java.util.Map.of("userId", Objects.requireNonNull(principal)));
    }
}
