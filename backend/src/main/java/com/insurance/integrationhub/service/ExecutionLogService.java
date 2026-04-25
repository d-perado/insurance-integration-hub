package com.insurance.integrationhub.service;

import com.insurance.integrationhub.common.exception.ServiceErrorCode;
import com.insurance.integrationhub.common.exception.ServiceException;
import com.insurance.integrationhub.domain.ExecutionLog;
import com.insurance.integrationhub.domain.FailureType;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;
import com.insurance.integrationhub.repository.ExecutionLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExecutionLogService {

    private final ExecutionLogRepository executionLogRepository;

    public List<ExecutionLogResponse> getExecutionLogs(
            Long interfaceId,
            InterfaceStatus status,
            FailureType failureType
    ) {
        List<ExecutionLog> logs = executionLogRepository.findAllByOrderByExecutedAtDesc();

        return logs.stream()
                .filter(log -> interfaceId == null ||
                        log.getInterfaceSystem().getId().equals(interfaceId))
                .filter(log -> status == null ||
                        log.getStatus() == status)
                .filter(log -> failureType == null ||
                        log.getFailureType() == failureType)
                .map(ExecutionLogResponse::from)
                .toList();
    }

    public ExecutionLogResponse getExecutionLog(Long logId) {
        ExecutionLog log = executionLogRepository.findById(logId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.LOG_NOT_FOUND));

        return ExecutionLogResponse.from(log);
    }
}