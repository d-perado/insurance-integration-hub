package com.insurance.integrationhub.dto.interfaces;

import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;

import java.util.List;

public record InterfaceDetailResponse(
        InterfaceResponse interfaceInfo,
        List<ExecutionLogResponse> recentLogs
) {
}