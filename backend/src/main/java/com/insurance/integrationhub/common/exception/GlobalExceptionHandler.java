package com.insurance.integrationhub.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<ErrorResponse> handleServiceException(ServiceException e) {
        ServiceErrorCode errorCode = e.getErrorCode();

        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.from(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(
            MethodArgumentNotValidException e
    ) {
        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .orElse(ServiceErrorCode.VALIDATION_ERROR.getMessage());

        return ResponseEntity
                .status(ServiceErrorCode.VALIDATION_ERROR.getStatus())
                .body(ErrorResponse.of(ServiceErrorCode.VALIDATION_ERROR, message));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException e
    ) {
        return ResponseEntity
                .status(ServiceErrorCode.INVALID_ENUM_VALUE.getStatus())
                .body(ErrorResponse.from(ServiceErrorCode.INVALID_ENUM_VALUE));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotAllowedException(
            HttpRequestMethodNotSupportedException e
    ) {
        return ResponseEntity
                .status(ServiceErrorCode.METHOD_NOT_ALLOWED.getStatus())
                .body(ErrorResponse.from(ServiceErrorCode.METHOD_NOT_ALLOWED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        return ResponseEntity
                .status(ServiceErrorCode.INTERNAL_SERVER_ERROR.getStatus())
                .body(ErrorResponse.from(ServiceErrorCode.INTERNAL_SERVER_ERROR));
    }
}