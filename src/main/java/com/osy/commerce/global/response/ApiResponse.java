package com.osy.commerce.global.response;

import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse {
    private final boolean success;
    private final ApiCode code;
    private final String message;
    private final Object data;

    public static ApiResponse of(ApiCode apiCode, @Nullable Object data, @Nullable String overrideMessage) {
        return ApiResponse.builder()
                .success(apiCode.getStatus().is2xxSuccessful())
                .code(apiCode)
                .message(overrideMessage != null ? overrideMessage : apiCode.getDefaultMessage())
                .data(data)
                .build();
    }

    public static ApiResponse ok(@Nullable Object data) {
        return of(ApiCode.OK, data, null);
    }

    public static ApiResponse error(ApiCode code, @Nullable String message) {
        return of(code, null, message);
    }
}
