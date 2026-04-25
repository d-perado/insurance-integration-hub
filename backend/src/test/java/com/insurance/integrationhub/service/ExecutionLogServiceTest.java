package com.insurance.integrationhub.service;

import com.insurance.integrationhub.common.exception.ServiceErrorCode;
import com.insurance.integrationhub.common.exception.ServiceException;
import com.insurance.integrationhub.domain.*;
import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;
import com.insurance.integrationhub.repository.ExecutionLogRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExecutionLogServiceTest {

    private final ExecutionLogRepository executionLogRepository = mock(ExecutionLogRepository.class);

    private final ExecutionLogService executionLogService =
            new ExecutionLogService(executionLogRepository);

    @Test
    @DisplayName("실행 로그 목록을 조회한다")
    void getExecutionLogs() {
        ExternalOrganization organization = new ExternalOrganization(
                "국민은행",
                "홍길동",
                "manager@kb.com"
        );

        InterfaceSystem interfaceSystem = new InterfaceSystem(
                organization,
                "보험료 납입 결과 수신",
                ProtocolType.REST,
                InterfaceStatus.FAILED,
                "계약운영팀",
                "https://api.kb.com/payment-result",
                "보험료 납입 결과 수신",
                LocalDateTime.now(),
                3000,
                10
        );

        ExecutionLog log = new ExecutionLog(
                interfaceSystem,
                LocalDateTime.now(),
                InterfaceStatus.FAILED,
                3000,
                "{\"policyNo\":\"P-001\"}",
                "{\"code\":\"TIMEOUT\"}",
                "외부 기관 응답 시간 초과",
                FailureType.NETWORK,
                "외부 기관 응답 지연 또는 네트워크 문제가 발생했습니다.",
                "1회 재실행 후 동일 오류 반복 시 확인 요청하세요.",
                0
        );

        when(executionLogRepository.findAllByOrderByExecutedAtDesc())
                .thenReturn(List.of(log));

        List<ExecutionLogResponse> result = executionLogService.getExecutionLogs(
                null,
                InterfaceStatus.FAILED,
                FailureType.NETWORK
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).status()).isEqualTo(InterfaceStatus.FAILED);
        assertThat(result.get(0).failureType()).isEqualTo(FailureType.NETWORK);
        assertThat(result.get(0).requestPayload()).contains("policyNo");
    }

    @Test
    @DisplayName("실행 로그 상세를 조회한다")
    void getExecutionLog() {
        ExternalOrganization organization = new ExternalOrganization(
                "국민은행",
                "홍길동",
                "manager@kb.com"
        );

        InterfaceSystem interfaceSystem = new InterfaceSystem(
                organization,
                "보험료 납입 결과 수신",
                ProtocolType.REST,
                InterfaceStatus.FAILED,
                "계약운영팀",
                "https://api.kb.com/payment-result",
                "보험료 납입 결과 수신",
                LocalDateTime.now(),
                3000,
                10
        );

        ExecutionLog log = new ExecutionLog(
                interfaceSystem,
                LocalDateTime.now(),
                InterfaceStatus.FAILED,
                3000,
                "{\"policyNo\":\"P-001\"}",
                "{\"code\":\"TIMEOUT\"}",
                "외부 기관 응답 시간 초과",
                FailureType.NETWORK,
                "외부 기관 응답 지연 또는 네트워크 문제가 발생했습니다.",
                "1회 재실행 후 동일 오류 반복 시 확인 요청하세요.",
                0
        );

        when(executionLogRepository.findById(1L)).thenReturn(Optional.of(log));

        ExecutionLogResponse result = executionLogService.getExecutionLog(1L);

        assertThat(result.interfaceName()).isEqualTo("보험료 납입 결과 수신");
        assertThat(result.responsePayload()).contains("TIMEOUT");
        assertThat(result.failureReason()).contains("네트워크");
    }

    @Test
    @DisplayName("존재하지 않는 로그 조회 시 예외가 발생한다")
    void getExecutionLog_notFound() {
        when(executionLogRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> executionLogService.getExecutionLog(999L))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.LOG_NOT_FOUND.getMessage());
    }
}