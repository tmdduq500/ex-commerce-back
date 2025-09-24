package com.osy.commerce.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse<T> {
    private String code;   // OK or ERR_*
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>("OK", "success", data);
    }

    public static ApiResponse<Void> ok() {
        return new ApiResponse<>("OK", "success", null);
    }

    public static ApiResponse<Void> error() {
        return new ApiResponse<>("ERROR", "fail", null);
    }

    public static ApiResponse<Void> error(String msg) {
        return new ApiResponse<>("ERROR", msg, null);
    }

    public static ApiResponse<Void> error(String code, String msg) {
        return new ApiResponse<>(code, msg, null);
    }
}
