package com.osy.commerce.auth.service;

import com.osy.commerce.auth.dto.*;
import com.osy.commerce.global.security.jwt.JwtTokenProvider;
import com.osy.commerce.global.security.jwt.RefreshTokenStore;
import com.osy.commerce.user.domain.AuthProvider;
import com.osy.commerce.user.domain.Role;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.domain.UserStatus;
import com.osy.commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwt;
    private final RefreshTokenStore refreshStore;

    public LoginResponse signup(SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("이미 가입된 이메일입니다.");
        }
        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .role(Role.ROLE_USER)
                .status(UserStatus.ACTIVE)
                .provider(AuthProvider.LOCAL)
                .providerId(null)
                .lastLoginAt(null)
                .build();
        userRepository.save(user);

        String access = jwt.createAccessToken(
                user.getId(),
                java.util.List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        String refresh = UUID.randomUUID().toString();
        refreshStore.save(user.getId(), refresh, jwt.getRefreshExpSeconds());

        return new LoginResponse(
                access, jwt.getAccessExpSeconds(),
                refresh, jwt.getRefreshExpSeconds(),
                new UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        java.util.Set.of(user.getRole().name())
                )
        );
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다."));
        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        String access = jwt.createAccessToken(
                user.getId(),
                java.util.List.of(new SimpleGrantedAuthority(user.getRole().name()))
        );
        String refresh = UUID.randomUUID().toString();
        refreshStore.save(user.getId(), refresh, jwt.getRefreshExpSeconds());

        return new LoginResponse(
                access, jwt.getAccessExpSeconds(),
                refresh, jwt.getRefreshExpSeconds(),
                new UserInfo(
                        user.getId(), user.getEmail(), user.getName(),
                        java.util.Set.of(user.getRole().name()))
        );
    }

    public TokenPairResponse refreshByToken(String refreshToken) {
        Long userId = refreshStore.findUserByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        String newAccess = jwt.createAccessToken(user.getId(),
                java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(user.getRole().name())));
        String newRefresh = UUID.randomUUID().toString(); // 회전
        refreshStore.save(user.getId(), newRefresh, jwt.getRefreshExpSeconds());

        return new TokenPairResponse(newAccess, jwt.getAccessExpSeconds(), newRefresh, jwt.getRefreshExpSeconds());
    }

    public void logout(Long userId) {
        refreshStore.deleteByUser(userId);
    }
}
