package com.osy.commerce.global.error;

import com.osy.commerce.global.response.ApiCode;
import com.osy.commerce.global.response.ApiResponse;
import com.osy.commerce.global.response.Responses;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.List;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse> handleApi(ApiException e) {
        return Responses.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException e) {
        return Responses.error(ApiCode.BAD_CREDENTIALS, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleBind(MethodArgumentNotValidException e) {
        List<ValidationError> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> new ValidationError(
                        fe.getField(), null, fe.getCode(),
                        fe.getDefaultMessage(), fe.getRejectedValue()))
                .toList();
        return Responses.error(ApiCode.VALIDATION_ERROR,
                ApiCode.VALIDATION_ERROR.getDefaultMessage(),
                Map.of("errors", errors));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse> handleConstraint(ConstraintViolationException e) {
        List<ValidationError> errors = e.getConstraintViolations().stream()
                .map(cv -> new ValidationError(
                        null,
                        cv.getPropertyPath() != null ? cv.getPropertyPath().toString() : null,
                        cv.getConstraintDescriptor() != null
                                ? cv.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()
                                : null,
                        cv.getMessage(),
                        cv.getInvalidValue()))
                .toList();
        return Responses.error(ApiCode.VALIDATION_ERROR,
                ApiCode.VALIDATION_ERROR.getDefaultMessage(),
                Map.of("errors", errors));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ValidationError ve = new ValidationError(
                null, e.getName(), "TypeMismatch",
                "파라미터 타입이 올바르지 않습니다.", e.getValue());
        return Responses.error(ApiCode.VALIDATION_ERROR,
                ApiCode.VALIDATION_ERROR.getDefaultMessage(),
                Map.of("errors", List.of(ve)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse> handleNotReadable(HttpMessageNotReadableException e) {
        return Responses.error(ApiCode.VALIDATION_ERROR, "요청 본문을 읽을 수 없습니다.", null);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse> handleAccessDenied(AccessDeniedException e) {
        return Responses.error(ApiCode.ACCESS_DENIED, e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> handleGeneric(Exception e) {
        log.error("Unhandled exception", e);
        return Responses.error(ApiCode.INTERNAL_ERROR, null);
    }
}
