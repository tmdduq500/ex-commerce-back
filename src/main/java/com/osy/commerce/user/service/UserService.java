package com.osy.commerce.user.service;

import com.osy.commerce.global.error.ApiException;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.global.security.jwt.RefreshTokenStore;
import com.osy.commerce.user.domain.User;
import com.osy.commerce.user.dto.UserProfileDto;
import com.osy.commerce.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenStore refreshTokenStore;

    public UserProfileDto getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자 없음"));

        Set<String> roles = user.getRoles().stream()
                .map(Enum::name)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        return new UserProfileDto(
                user.getId(),
                user.getEmail(),
                user.getName(),
                roles,
                user.getStatus().name(),
                user.getLastLoginAt()
        );
    }

    @Transactional
    public void changePassword(Long userId, String current, String next) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(ApiCode.USER_NOT_FOUND, "사용자를 찾을 수 없습니다."));
        if (!passwordEncoder.matches(current, user.getPassword())) {
            throw new ApiException(ApiCode.BAD_CREDENTIALS, "현재 비밀번호가 일치하지 않습니다.");
        }
        user.updatePassword(passwordEncoder.encode(next));
        refreshTokenStore.deleteByUser(userId);
    }

}

