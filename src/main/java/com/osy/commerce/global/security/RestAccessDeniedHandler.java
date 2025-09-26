package com.osy.commerce.global.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.global.response.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper om = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest req, HttpServletResponse res, AccessDeniedException ex) throws IOException {
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setContentType("application/json;charset=UTF-8");
        ApiResponse body = ApiResponse.error(ApiCode.ACCESS_DENIED, ApiCode.ACCESS_DENIED.getDefaultMessage());
        om.writeValue(res.getWriter(), body);
    }
}
