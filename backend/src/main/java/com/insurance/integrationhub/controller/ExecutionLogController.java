package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.dto.common.PageResponse;
import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;
import com.insurance.integrationhub.service.ExecutionLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Execution Log", description = "인터페이스 실행 이력 API")
@RestController
@RequestMapping("/api/executions")
@RequiredArgsConstructor
public class ExecutionLogController {

    private final ExecutionLogService executionLogService;

    @Operation(summary = "실행 이력 목록 조회", description = "인터페이스 실행 이력을 조건별로 조회합니다. 페이지네이션을 지원합니다.")
    @GetMapping
    public ApiResponse<PageResponse<ExecutionLogResponse>> getExecutionLogs(
            @Parameter(description = "인터페이스 ID", example = "1")
            @RequestParam(required = false) Long interfaceId,

            @Parameter(description = "실행 상태")
            @RequestParam(required = false) InterfaceStatus status,

            @Parameter(description = "실패 유형")
            @RequestParam(required = false) FailureType failureType,

            @Parameter(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Parameter(description = "페이지 크기", example = "10")
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

    @Operation(summary = "실행 이력 상세 조회", description = "실행 로그 ID로 상세 실행 이력을 조회합니다.")
    @GetMapping("/{logId}")
    public ApiResponse<ExecutionLogResponse> getExecutionLog(
            @Parameter(description = "실행 로그 ID", example = "1")
            @PathVariable Long logId
    ) {
        ExecutionLogResponse response = executionLogService.getExecutionLog(logId);
        return ApiResponse.success(response);
    }
}