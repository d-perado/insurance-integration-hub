package com.insurance.integrationhub.service;

import com.insurance.integrationhub.common.exception.ServiceErrorCode;
import com.insurance.integrationhub.common.exception.ServiceException;
import com.insurance.integrationhub.domain.*;
import com.insurance.integrationhub.dto.interfaces.CreateInterfaceSystemRequest;
import com.insurance.integrationhub.dto.interfaces.InterfaceDetailResponse;
import com.insurance.integrationhub.dto.interfaces.InterfaceResponse;
import com.insurance.integrationhub.dto.interfaces.UpdateInterfaceSystemRequest;
import com.insurance.integrationhub.repository.ExecutionLogRepository;
import com.insurance.integrationhub.repository.ExternalOrganizationRepository;
import com.insurance.integrationhub.repository.InterfaceSystemRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class InterfaceSystemServiceTest {

    private final InterfaceSystemRepository interfaceSystemRepository =
            mock(InterfaceSystemRepository.class);

    private final ExecutionLogRepository executionLogRepository =
            mock(ExecutionLogRepository.class);

    private final ExternalOrganizationRepository externalOrganizationRepository =
            mock(ExternalOrganizationRepository.class);

    private final InterfaceSystemService interfaceSystemService =
            new InterfaceSystemService(
                    interfaceSystemRepository,
                    executionLogRepository,
                    externalOrganizationRepository
            );

    @Test
    @DisplayName("인터페이스 목록을 조회한다")
    void getInterfaces() {
        ExternalOrganization organization = new ExternalOrganization(
                "국민은행",
                "홍길동",
                "manager@kb.com"
        );

        InterfaceSystem interfaceSystem = new InterfaceSystem(
                organization,
                "보험료 납입 결과 수신",
                ProtocolType.REST,
                InterfaceStatus.SUCCESS,
                "계약운영팀",
                "https://api.kb.com/payment-result",
                "보험료 납입 결과를 수신하는 인터페이스",
                LocalDateTime.now(),
                120,
                35
        );

        when(interfaceSystemRepository.findAll()).thenReturn(List.of(interfaceSystem));

        List<InterfaceResponse> result = interfaceSystemService.getInterfaces(
                InterfaceStatus.SUCCESS,
                ProtocolType.REST,
                "국민"
        );

        assertThat(result).hasSize(1);
        assertThat(result.get(0).organization()).isEqualTo("국민은행");
        assertThat(result.get(0).protocol()).isEqualTo(ProtocolType.REST);
        assertThat(result.get(0).status()).isEqualTo(InterfaceStatus.SUCCESS);
    }

    @Test
    @DisplayName("인터페이스 상세를 조회한다")
    void getInterface() {
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
                "보험료 납입 결과를 수신하는 인터페이스",
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
                "외부 기관 응답 지연 또는 네트워크 연결 문제가 발생했습니다.",
                "1회 재실행 후 동일 오류가 반복되면 기관 담당자에게 확인 요청하세요.",
                0
        );

        when(interfaceSystemRepository.findById(1L)).thenReturn(Optional.of(interfaceSystem));
        when(executionLogRepository.findTop5ByInterfaceSystemIdOrderByExecutedAtDesc(1L))
                .thenReturn(List.of(log));

        InterfaceDetailResponse result = interfaceSystemService.getInterface(1L);

        assertThat(result.interfaceInfo().name()).isEqualTo("보험료 납입 결과 수신");
        assertThat(result.recentLogs()).hasSize(1);
        assertThat(result.recentLogs().get(0).failureType()).isEqualTo(FailureType.NETWORK);
    }

    @Test
    @DisplayName("존재하지 않는 인터페이스 상세 조회 시 예외가 발생한다")
    void getInterface_notFound() {
        when(interfaceSystemRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> interfaceSystemService.getInterface(999L))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.INTERFACE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("중복된 인터페이스명으로 등록 시 예외가 발생한다")
    void createInterfaceSystem_duplicateName() {
        CreateInterfaceSystemRequest request = createInterfaceRequest();

        when(interfaceSystemRepository.existsByName(request.name()))
                .thenReturn(true);

        assertThatThrownBy(() ->
                interfaceSystemService.createInterfaceSystem(request)
        )
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.INTERFACE_NAME_DUPLICATED.getMessage());

        verify(interfaceSystemRepository, never()).save(any());
    }

    @Test
    @DisplayName("중복된 인터페이스명으로 수정 시 예외가 발생한다")
    void updateInterfaceSystem_duplicateName() {
        InterfaceSystem interfaceSystem = createInterfaceSystem();

        UpdateInterfaceSystemRequest request = new UpdateInterfaceSystemRequest(
                1L,
                "계약 상태 변경 송신",
                ProtocolType.SFTP,
                "고객관리팀",
                "sftp://shinhan.com/contract-status",
                "계약 상태 변경 정보를 외부 기관으로 송신"
        );

        when(interfaceSystemRepository.findById(1L))
                .thenReturn(Optional.of(interfaceSystem));

        when(interfaceSystemRepository.existsByName("계약 상태 변경 송신"))
                .thenReturn(true);

        assertThatThrownBy(() ->
                interfaceSystemService.updateInterfaceSystem(1L, request)
        )
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.INTERFACE_NAME_DUPLICATED.getMessage());
    }

    @Test
    @DisplayName("기존 인터페이스명과 동일한 이름으로 수정할 수 있다")
    void updateInterfaceSystem_sameName() {
        InterfaceSystem interfaceSystem = createInterfaceSystem();
        ExternalOrganization organization = createOrganization();

        UpdateInterfaceSystemRequest request = new UpdateInterfaceSystemRequest(
                1L,
                "보험료 납입 결과 수신",
                ProtocolType.REST,
                "계약운영팀",
                "https://api.kb.com/payment-result-v2",
                "보험료 납입 결과 수신 endpoint 수정"
        );

        when(interfaceSystemRepository.findById(1L))
                .thenReturn(Optional.of(interfaceSystem));

        when(externalOrganizationRepository.findById(1L))
                .thenReturn(Optional.of(organization));

        InterfaceResponse result =
                interfaceSystemService.updateInterfaceSystem(1L, request);

        assertThat(result.name()).isEqualTo("보험료 납입 결과 수신");
        assertThat(result.endpoint()).isEqualTo("https://api.kb.com/payment-result-v2");

        verify(interfaceSystemRepository, never())
                .existsByName("보험료 납입 결과 수신");
    }

    @Test
    @DisplayName("FAILED 상태의 인터페이스는 재실행할 수 있다")
    void retryInterface() {
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
                "보험료 납입 결과를 수신하는 인터페이스",
                LocalDateTime.now(),
                3000,
                10
        );

        when(interfaceSystemRepository.findById(1L)).thenReturn(Optional.of(interfaceSystem));

        InterfaceResponse result = interfaceSystemService.retryInterface(1L);

        assertThat(result.status()).isIn(InterfaceStatus.SUCCESS, InterfaceStatus.FAILED);
        assertThat(result.todayCount()).isEqualTo(11);

        ArgumentCaptor<ExecutionLog> captor = ArgumentCaptor.forClass(ExecutionLog.class);
        verify(executionLogRepository).save(captor.capture());

        ExecutionLog savedLog = captor.getValue();

        assertThat(savedLog.getInterfaceSystem()).isEqualTo(interfaceSystem);
        assertThat(savedLog.getRetryCount()).isEqualTo(1);
        assertThat(savedLog.getStatus()).isIn(InterfaceStatus.SUCCESS, InterfaceStatus.FAILED);
    }

    @Test
    @DisplayName("FAILED 상태가 아닌 인터페이스는 재실행할 수 없다")
    void retryInterface_retryNotAllowed() {
        ExternalOrganization organization = new ExternalOrganization(
                "국민은행",
                "홍길동",
                "manager@kb.com"
        );

        InterfaceSystem interfaceSystem = new InterfaceSystem(
                organization,
                "보험료 납입 결과 수신",
                ProtocolType.REST,
                InterfaceStatus.SUCCESS,
                "계약운영팀",
                "https://api.kb.com/payment-result",
                "보험료 납입 결과를 수신하는 인터페이스",
                LocalDateTime.now(),
                120,
                10
        );

        when(interfaceSystemRepository.findById(1L)).thenReturn(Optional.of(interfaceSystem));

        assertThatThrownBy(() -> interfaceSystemService.retryInterface(1L))
                .isInstanceOf(ServiceException.class)
                .hasMessage(ServiceErrorCode.RETRY_NOT_ALLOWED.getMessage());

        verify(executionLogRepository, never()).save(any());
    }

    private ExternalOrganization createOrganization() {
        return new ExternalOrganization(
                "국민은행",
                "홍길동",
                "manager@kb.com"
        );
    }

    private InterfaceSystem createInterfaceSystem() {
        return new InterfaceSystem(
                createOrganization(),
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
    }

    private CreateInterfaceSystemRequest createInterfaceRequest() {
        return new CreateInterfaceSystemRequest(
                1L,
                "보험료 납입 결과 수신",
                ProtocolType.REST,
                "계약운영팀",
                "https://api.kb.com/payment-result",
                "보험료 납입 결과를 외부 기관으로부터 수신"
        );
    }
}