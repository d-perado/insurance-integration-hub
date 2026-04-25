package com.insurance.integrationhub.dto.executions;

import com.insurance.integrationhub.domain.ExecutionLog;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;

import java.time.LocalDateTime;

public record ExecutionLogResponse(
        Long id,
        Long interfaceId,
        String interfaceName,
        LocalDateTime executedAt,
        InterfaceStatus status,
        Integer responseTimeMs,
        String requestPayload,
        String responsePayload,
        String errorMessage,
        FailureType failureType,
        String failureReason,
        String suggestedAction,
        Integer retryCount
) {
    public static ExecutionLogResponse from(ExecutionLog log) {
        return new ExecutionLogResponse(
                log.getId(),
                log.getInterfaceSystem().getId(),
                log.getInterfaceSystem().getName(),
                log.getExecutedAt(),
                log.getStatus(),
                log.getResponseTimeMs(),
                log.getRequestPayload(),
                log.getResponsePayload(),
                log.getErrorMessage(),
                log.getFailureType(),
                log.getFailureReason(),
                log.getSuggestedAction(),
                log.getRetryCount()
        );
    }
}