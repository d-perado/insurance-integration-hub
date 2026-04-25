package com.insurance.integrationhub.common.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
        boolean success,
        String code,
        String message,
        T data,
        LocalDateTime timestamp
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                "요청이 정상 처리되었습니다.",
                data,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                message,
                data,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(
                true,
                "CREATED",
                "정상적으로 생성되었습니다.",
                data,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return new ApiResponse<>(
                true,
                "CREATED",
                message,
                data,
                LocalDateTime.now()
        );
    }

    public static <T> ApiResponse<T> empty(String message) {
        return new ApiResponse<>(
                true,
                "SUCCESS",
                message,
                null,
                LocalDateTime.now()
        );
    }
}