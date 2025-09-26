package com.osy.commerce.global.security.jwt;

import com.osy.commerce.user.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // 1) CORS Preflight는 바로 통과
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            chain.doFilter(request, response);
            return;
        }

        // 2) 이미 인증 있으면 패스
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            chain.doFilter(request, response);
            return;
        }

        // 3) Authorization 헤더 검사
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String token = header.substring(7);

        try {
            // 4) 토큰 검증 (유효/서명/만료)
            if (!jwtTokenProvider.validate(token)) {
                chain.doFilter(request, response);
                return;
            }

            // 5) 토큰에서 subject, 권한 꺼내 Authentication 구성
            Long userId = jwtTokenProvider.getUserId(token);
            Collection<? extends GrantedAuthority> authorities = jwtTokenProvider.getAuthorities(token);

            if (userId != null) {
                UsernamePasswordAuthenticationToken auth =
                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            // 액세스 토큰 만료: 인증 세팅 없이 통과 → 인증 필요한 엔드포인트에서 EntryPoint가 401 응답
             log.debug("JWT expired uri={}", request.getRequestURI());
        } catch (io.jsonwebtoken.JwtException | IllegalArgumentException e) {
            // 서명 오류/형식 오류 등: 인증 세팅 없이 통과
             log.warn("JWT invalid: {} uri={}", e.getMessage(), request.getRequestURI());
        }

        // 6) 다음 필터
        chain.doFilter(request, response);
    }

}
