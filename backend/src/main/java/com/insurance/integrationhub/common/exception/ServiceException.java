package com.insurance.integrationhub.common.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {

    private final ServiceErrorCode errorCode;

    public ServiceException(ServiceErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}