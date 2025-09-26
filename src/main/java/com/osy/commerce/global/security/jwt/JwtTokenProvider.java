package com.osy.commerce.global.security.jwt;

import com.osy.commerce.user.domain.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-ttl-seconds:3600}")
    private long accessTtlSeconds;

    @Value("${jwt.refresh-ttl-seconds:604800}")
    private long refreshTtlSeconds;

    private Key key;

    @Getter
    private long accessExpSeconds;
    @Getter
    private long refreshExpSeconds;

    @jakarta.annotation.PostConstruct
    void init() {
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException e) {
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpSeconds = accessTtlSeconds;
        this.refreshExpSeconds = refreshTtlSeconds;
    }

    /** 다중 Role → AccessToken 생성 */
    public String createAccessToken(Long userId, Set<Role> roles) {
        String authClaim = Optional.ofNullable(roles)
                .orElseGet(Set::of)
                .stream().map(Role::name).distinct().sorted()
                .collect(Collectors.joining(","));

        Date now = new Date();
        Date exp = new Date(now.getTime() + Duration.ofSeconds(accessTtlSeconds).toMillis());

        return Jwts.builder()
                .setSubject(String.valueOf(userId))
                .claim("auth", authClaim)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /** 토큰에서 userId(subject) 추출 */
    public Long getUserId(String token) {
        Claims c = parseClaims(token);
        String sub = c.getSubject();
        if (!StringUtils.hasText(sub)) return null;
        try {
            return Long.parseLong(sub);
        } catch (NumberFormatException e) {
            log.warn("Invalid JWT subject: {}", sub);
            return null;
        }
    }

    /** 만료된 토큰이어도 userId가 필요할 때 사용( */
    public Long getUserIdAllowExpired(String token) {
        try {
            return getUserId(token);
        } catch (ExpiredJwtException eje) {
            String sub = eje.getClaims().getSubject();
            try {
                return Long.parseLong(sub);
            } catch (Exception ignore) {
                return null;
            }
        }
    }

    public boolean validate(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public List<SimpleGrantedAuthority> getAuthorities(String token) {
        Claims c = parseClaims(token);
        String auth = c.get("auth", String.class);
        if (!StringUtils.hasText(auth)) return List.of();

        return Arrays.stream(auth.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(r -> r.startsWith("ROLE_") ? r : "ROLE_" + r)
                .map(org.springframework.security.core.authority.SimpleGrantedAuthority::new)
                .toList();
    }

    public Claims parseClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)   // 유효성 + 서명 검증
                .getBody();
    }

    public Claims parseClaimsAllowExpired(String token) {
        try {
            return parseClaims(token);
        } catch (ExpiredJwtException eje) {
            return eje.getClaims();
        }
    }
}
