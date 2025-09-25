package com.osy.commerce.global.error;

import com.osy.commerce.global.response.ApiCode;
import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {
    private final ApiCode code;

    public ApiException(ApiCode code, String message) {
        super(message);
        this.code = code;
    }

    public ApiException(ApiCode code) {
        this(code, code.getDefaultMessage());
    }
}
