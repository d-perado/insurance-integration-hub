package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.dto.common.PageResponse;
import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;
import com.insurance.integrationhub.service.ExecutionLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/executions")
@RequiredArgsConstructor
public class ExecutionLogController {

    private final ExecutionLogService executionLogService;

    @GetMapping
    public ApiResponse<PageResponse<ExecutionLogResponse>> getExecutionLogs(
            @RequestParam(required = false) Long interfaceId,
            @RequestParam(required = false) InterfaceStatus status,
            @RequestParam(required = false) FailureType failureType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);

        PageResponse<ExecutionLogResponse> response =
                executionLogService.getExecutionLogs(
                        interfaceId,
                        status,
                        failureType,
                        pageable
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