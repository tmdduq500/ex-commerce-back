package com.osy.commerce.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public final class ValidationError {
    private String field;
    private String parameter;
    private String code;
    private String message;
    private Object rejectedValue;
}
