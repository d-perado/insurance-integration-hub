package com.insurance.integrationhub.service;

import com.insurance.integrationhub.common.exception.ServiceErrorCode;
import com.insurance.integrationhub.common.exception.ServiceException;
import com.insurance.integrationhub.domain.*;
import com.insurance.integrationhub.dto.executions.ExecutionLogResponse;
import com.insurance.integrationhub.dto.interfaces.CreateInterfaceSystemRequest;
import com.insurance.integrationhub.dto.interfaces.InterfaceDetailResponse;
import com.insurance.integrationhub.dto.interfaces.InterfaceResponse;
import com.insurance.integrationhub.dto.interfaces.UpdateInterfaceSystemRequest;
import com.insurance.integrationhub.repository.ExecutionLogRepository;
import com.insurance.integrationhub.repository.ExternalOrganizationRepository;
import com.insurance.integrationhub.repository.InterfaceSystemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InterfaceSystemService {

    private final InterfaceSystemRepository interfaceSystemRepository;
    private final ExecutionLogRepository executionLogRepository;
    private final ExternalOrganizationRepository externalOrganizationRepository;

    @Transactional
    public InterfaceResponse createInterfaceSystem(CreateInterfaceSystemRequest request) {
        validateDuplicateName(request.name());

        ExternalOrganization organization = externalOrganizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.ORGANIZATION_NOT_FOUND));

        InterfaceSystem interfaceSystem = new InterfaceSystem(
                organization,
                request.name(),
                request.protocolType(),
                InterfaceStatus.PENDING,
                request.ownerTeam(),
                request.endpoint(),
                request.description(),
                null,
                null,
                0
        );

        InterfaceSystem savedInterfaceSystem =
                interfaceSystemRepository.save(interfaceSystem);

        return InterfaceResponse.from(savedInterfaceSystem);
    }

    public List<InterfaceResponse> getInterfaces(
            InterfaceStatus status,
            ProtocolType protocolType,
            String keyword
    ) {
        List<InterfaceSystem> interfaces = interfaceSystemRepository.findAll();

        return interfaces.stream()
                .filter(item -> status == null || item.getStatus() == status)
                .filter(item -> protocolType == null || item.getProtocolType() == protocolType)
                .filter(item -> keyword == null || keyword.isBlank()
                        || item.getName().contains(keyword)
                        || item.getExternalOrganization().getName().contains(keyword))
                .map(InterfaceResponse::from)
                .toList();
    }

    public InterfaceDetailResponse getInterface(Long interfaceId) {
        InterfaceSystem interfaceSystem = interfaceSystemRepository.findById(interfaceId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.INTERFACE_NOT_FOUND));

        List<ExecutionLogResponse> recentLogs = executionLogRepository
                .findTop5ByInterfaceSystemIdOrderByExecutedAtDesc(interfaceId)
                .stream()
                .map(ExecutionLogResponse::from)
                .toList();

        return new InterfaceDetailResponse(
                InterfaceResponse.from(interfaceSystem),
                recentLogs
        );
    }

    @Transactional
    public InterfaceResponse updateInterfaceSystem(
            Long interfaceId,
            UpdateInterfaceSystemRequest request
    ) {
        InterfaceSystem interfaceSystem = interfaceSystemRepository.findById(interfaceId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.INTERFACE_NOT_FOUND));

        if (!interfaceSystem.getName().equals(request.name())) {
            validateDuplicateName(request.name());
        }

        ExternalOrganization organization = externalOrganizationRepository.findById(request.organizationId())
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.ORGANIZATION_NOT_FOUND));

        interfaceSystem.update(
                organization,
                request.name(),
                request.protocolType(),
                request.ownerTeam(),
                request.endpoint(),
                request.description()
        );

        return InterfaceResponse.from(interfaceSystem);
    }

    private void validateDuplicateName(String name) {
        if (interfaceSystemRepository.existsByName(name)) {
            throw new ServiceException(ServiceErrorCode.INTERFACE_NAME_DUPLICATED);
        }
    }

    @Transactional
    public InterfaceResponse retryInterface(Long interfaceId) {
        InterfaceSystem interfaceSystem = interfaceSystemRepository.findById(interfaceId)
                .orElseThrow(() -> new ServiceException(ServiceErrorCode.INTERFACE_NOT_FOUND));

        if (interfaceSystem.getStatus() != InterfaceStatus.FAILED) {
            throw new ServiceException(ServiceErrorCode.RETRY_NOT_ALLOWED);
        }

        boolean success = new Random().nextBoolean();
        int responseTimeMs = success ? 180 : 3200;

        InterfaceStatus resultStatus = success
                ? InterfaceStatus.SUCCESS
                : InterfaceStatus.FAILED;

        ExecutionLog log = createRetryLog(interfaceSystem, resultStatus, responseTimeMs);
        executionLogRepository.save(log);

        interfaceSystem.updateStatus(resultStatus);
        interfaceSystem.updateMetrics(responseTimeMs);

        return InterfaceResponse.from(interfaceSystem);
    }

    private ExecutionLog createRetryLog(
            InterfaceSystem interfaceSystem,
            InterfaceStatus resultStatus,
            int responseTimeMs
    ) {
        if (resultStatus == InterfaceStatus.SUCCESS) {
            return new ExecutionLog(
                    interfaceSystem,
                    LocalDateTime.now(),
                    InterfaceStatus.SUCCESS,
                    responseTimeMs,
                    """
                    {
                      "policyNo": "P-2026-001",
                      "requestType": "RETRY"
                    }
                    """,
                    """
                    {
                      "code": "SUCCESS",
                      "message": "정상 처리되었습니다."
                    }
                    """,
                    null,
                    FailureType.NONE,
                    null,
                    null,
                    1
            );
        }

        return new ExecutionLog(
                interfaceSystem,
                LocalDateTime.now(),
                InterfaceStatus.FAILED,
                responseTimeMs,
                """
                {
                  "policyNo": "P-2026-001",
                  "requestType": "RETRY"
                }
                """,
                """
                {
                  "code": "TIMEOUT",
                  "message": "외부 기관 응답 시간이 초과되었습니다."
                }
                """,
                "외부 기관 응답 시간 초과",
                FailureType.NETWORK,
                "외부 기관 응답 지연 또는 네트워크 연결 문제가 발생했습니다.",
                "1회 재실행 후 동일 오류가 반복되면 기관 담당자 또는 인프라팀에 확인 요청하세요.",
                1
        );
    }
}