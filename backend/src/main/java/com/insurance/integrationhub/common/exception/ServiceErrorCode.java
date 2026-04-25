package com.insurance.integrationhub.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ServiceErrorCode {

    // Common
    INVALID_REQUEST(HttpStatus.BAD_REQUEST, "COMMON_001", "잘못된 요청입니다."),
    VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON_002", "요청 값 검증에 실패했습니다."),
    INVALID_ENUM_VALUE(HttpStatus.BAD_REQUEST, "COMMON_003", "유효하지 않은 요청 값입니다."),
    METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_004", "지원하지 않는 HTTP 메서드입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_999", "서버 내부 오류가 발생했습니다."),

    // Organization
    ORGANIZATION_NOT_FOUND(HttpStatus.NOT_FOUND, "ORG_001", "외부 기관 정보를 찾을 수 없습니다."),
    ORGANIZATION_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "ORG_002", "이미 등록된 기관명입니다."),

    // Interface
    INTERFACE_NOT_FOUND(HttpStatus.NOT_FOUND, "INTERFACE_001", "인터페이스 정보를 찾을 수 없습니다."),
    INVALID_INTERFACE_STATUS(HttpStatus.BAD_REQUEST, "INTERFACE_002", "유효하지 않은 인터페이스 상태입니다."),
    INTERFACE_ALREADY_RUNNING(HttpStatus.CONFLICT, "INTERFACE_003", "이미 실행 중인 인터페이스입니다."),
    RETRY_NOT_ALLOWED(HttpStatus.CONFLICT, "INTERFACE_004", "실패 상태의 인터페이스만 재실행할 수 있습니다."),

    // Log
    LOG_NOT_FOUND(HttpStatus.NOT_FOUND, "LOG_001", "실행 로그를 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;

    ServiceErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}