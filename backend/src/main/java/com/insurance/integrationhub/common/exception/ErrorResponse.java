package com.insurance.integrationhub.common.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        boolean success,
        String code,
        String message,
        LocalDateTime timestamp
) {

    public static ErrorResponse from(ServiceErrorCode errorCode) {
        return new ErrorResponse(
                false,
                errorCode.getCode(),
                errorCode.getMessage(),
                LocalDateTime.now()
        );
    }

    public static ErrorResponse of(ServiceErrorCode errorCode, String message) {
        return new ErrorResponse(
                false,
                errorCode.getCode(),
                message,
                LocalDateTime.now()
        );
    }
}