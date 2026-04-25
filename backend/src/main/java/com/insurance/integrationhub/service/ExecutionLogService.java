package com.insurance.integrationhub.service;

import com.insurance.integrationhub.domain.ExecutionLog;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.dto.common.PageResponse;
import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;
import com.insurance.integrationhub.repository.ExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExecutionLogService {

    private final ExecutionLogRepository executionLogRepository;

    public PageResponse<ExecutionLogResponse> getExecutionLogs(
            Long interfaceId,
            InterfaceStatus status,
            FailureType failureType,
            Pageable pageable
    ) {
        Page<ExecutionLogResponse> page = executionLogRepository.findExecutionLogs(
                        interfaceId,
                        status,
                        failureType,
                        pageable
                )
                .map(ExecutionLogResponse::from);

        return PageResponse.from(page);
    }

    public ExecutionLogResponse getExecutionLog(Long logId) {
        ExecutionLog log = executionLogRepository.findById(logId)
                .orElseThrow(() -> new IllegalArgumentException("실행 로그를 찾을 수 없습니다."));

        return ExecutionLogResponse.from(log);
    }
}