package com.osy.commerce.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest req, HttpServletResponse res, org.springframework.security.core.AuthenticationException ex) throws IOException {
        res.setStatus(HttpStatus.UNAUTHORIZED.value());
        res.setContentType("application/json;charset=UTF-8");
        ApiResponse body = ApiResponse.error(ApiCode.AUTH_REQUIRED, ApiCode.AUTH_REQUIRED.getDefaultMessage());
        om.writeValue(res.getWriter(), body);
    }
}
