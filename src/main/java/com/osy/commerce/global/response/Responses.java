package com.osy.commerce.global.response;

import org.springframework.http.ResponseEntity;

public class Responses {
    public static ResponseEntity<ApiResponse> wrap(ApiCode code, Object data) {
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.of(code, data, null));
    }

    public static ResponseEntity<ApiResponse> ok(Object data) {
        return wrap(ApiCode.OK, data);
    }

    public static ResponseEntity<ApiResponse> ok() {
        return wrap(ApiCode.OK, null);
    }

    public static ResponseEntity<ApiResponse> created() {
        return wrap(ApiCode.CREATED, null);
    }

    public static ResponseEntity<ApiResponse> created(Object data) {
        return wrap(ApiCode.CREATED, data);
    }

    public static ResponseEntity<ApiResponse> error(ApiCode code, String message) {
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.error(code, message));
    }

    public static ResponseEntity<ApiResponse> error(ApiCode code, String message, Object payload) {
        return ResponseEntity.status(code.getStatus())
                .body(ApiResponse.of(code, payload, message));
    }
}
