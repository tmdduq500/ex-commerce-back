package com.osy.commerce.auth.service;

import com.osy.commerce.auth.dto.*;
import com.osy.commerce.global.error.ApiException;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.global.security.jwt.JwtTokenProvider;
import com.osy.commerce.global.security.jwt.RefreshTokenStore;
import com.osy.commerce.user.domain.AuthProvider;
import com.osy.commerce.user.domain.Role;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.domain.UserStatus;
import com.osy.commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;
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
            throw new ApiException(ApiCode.EMAIL_DUPLICATE, "이미 가입된 이메일입니다.");
        }

        User user = User.builder()
                .email(req.getEmail())
                .password(passwordEncoder.encode(req.getPassword()))
                .name(req.getName())
                .status(UserStatus.ACTIVE)
                .provider(AuthProvider.LOCAL)
                .providerId(null)
                .lastLoginAt(null)
                .build();
        user.addRole(Role.ROLE_USER);
        userRepository.save(user);

        String access = jwt.createAccessToken(user.getId(), user.getRoles());
        String refresh = UUID.randomUUID().toString();
        refreshStore.save(user.getId(), refresh, jwt.getRefreshExpSeconds());

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new LoginResponse(
                access, jwt.getAccessExpSeconds(),
                refresh, jwt.getRefreshExpSeconds(),
                new UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        roles
                )
        );
    }

    public LoginResponse login(LoginRequest req) {
        User user = userRepository.findByEmail(req.getEmail())
                .orElseThrow(() -> new ApiException(ApiCode.BAD_CREDENTIALS, "가입되지 않은 이메일입니다."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw new ApiException(ApiCode.BAD_CREDENTIALS, "이메일 또는 비밀번호가 올바르지 않습니다.");
        }

        user.updateLastLoginAt(LocalDateTime.now());

        String access = jwt.createAccessToken(user.getId(), user.getRoles());
        String refresh = UUID.randomUUID().toString();
        refreshStore.save(user.getId(), refresh, jwt.getRefreshExpSeconds());

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new LoginResponse(
                access, jwt.getAccessExpSeconds(),
                refresh, jwt.getRefreshExpSeconds(),
                new UserInfo(
                        user.getId(),
                        user.getEmail(),
                        user.getName(),
                        roles
                )
        );
    }

    public TokenPairResponse refreshByToken(String refreshToken) {
        Long userId = refreshStore.findUserByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        String newAccess = jwt.createAccessToken(user.getId(), user.getRoles());
        String newRefresh = UUID.randomUUID().toString();
        refreshStore.save(user.getId(), newRefresh, jwt.getRefreshExpSeconds());

        return new TokenPairResponse(newAccess, jwt.getAccessExpSeconds(), newRefresh, jwt.getRefreshExpSeconds());
    }

    public void logout(Long userId) {
        refreshStore.deleteByUser(userId);
    }
}
