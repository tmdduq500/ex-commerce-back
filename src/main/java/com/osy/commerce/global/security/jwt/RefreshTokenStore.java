package com.osy.commerce.global.security.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RefreshTokenStore {

    private static final String RTU = "RTU:"; // userId -> token
    private static final String RTK = "RTK:"; // token  -> userId
    private final StringRedisTemplate redis;

    public void save(Long userId, String token, long ttlSeconds) {
        // 기존 토큰 있으면 먼저 정리
        String old = redis.opsForValue().get(RTU + userId);
        if (old != null) redis.delete(RTK + old);

        redis.opsForValue().set(RTU + userId, token, Duration.ofSeconds(ttlSeconds));
        redis.opsForValue().set(RTK + token, String.valueOf(userId), Duration.ofSeconds(ttlSeconds));
    }

    public java.util.Optional<Long> findUserByToken(String token) {
        String v = redis.opsForValue().get(RTK + token);
        return v == null ? java.util.Optional.empty() : java.util.Optional.of(Long.valueOf(v));
    }

    public void deleteByUser(Long userId) {
        String tok = redis.opsForValue().get(RTU + userId);
        if (tok != null) redis.delete(RTK + tok);
        redis.delete(RTU + userId);
    }
}
