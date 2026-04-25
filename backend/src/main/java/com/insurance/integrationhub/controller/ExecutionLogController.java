package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;
import com.insurance.integrationhub.service.ExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/executions")
@RequiredArgsConstructor
public class ExecutionLogController {

    private final ExecutionLogService executionLogService;

    @GetMapping
    public ApiResponse<List<ExecutionLogResponse>> getExecutionLogs(
            @RequestParam(required = false) Long interfaceId,
            @RequestParam(required = false) InterfaceStatus status,
            @RequestParam(required = false) FailureType failureType
    ) {
        List<ExecutionLogResponse> response = executionLogService.getExecutionLogs(
                interfaceId,
                status,
                failureType
        );

        return ApiResponse.success(response);
    }

    @GetMapping("/{logId}")
    public ApiResponse<ExecutionLogResponse> getExecutionLog(
            @PathVariable Long logId
    ) {
        ExecutionLogResponse response = executionLogService.getExecutionLog(logId);
        return ApiResponse.success(response);
    }
}