package com.insurance.integrationhub.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "execution_logs",
        indexes = {
                @Index(name = "idx_log_interface_executed_at", columnList = "interface_system_id, executed_at"),
                @Index(name = "idx_log_status", columnList = "status"),
                @Index(name = "idx_log_failure_type", columnList = "failure_type")
        }
)
public class ExecutionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interface_system_id", nullable = false)
    private InterfaceSystem interfaceSystem;

    @Column(name = "executed_at", nullable = false)
    private LocalDateTime executedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private InterfaceStatus status;

    @Column(name = "response_time_ms")
    private Integer responseTimeMs;

    @Lob
    @Column(name = "request_payload", columnDefinition = "TEXT")
    private String requestPayload;

    @Lob
    @Column(name = "response_payload", columnDefinition = "TEXT")
    private String responsePayload;

    @Column(name = "error_message", length = 1000)
    private String errorMessage;

    @Enumerated(EnumType.STRING)
    @Column(name = "failure_type", nullable = false)
    private FailureType failureType;

    @Column(name = "failure_reason", length = 1000)
    private String failureReason;

    @Column(name = "suggested_action", length = 1000)
    private String suggestedAction;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;

    @Builder
    public ExecutionLog(
            InterfaceSystem interfaceSystem,
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
        this.interfaceSystem = interfaceSystem;
        this.executedAt = executedAt;
        this.status = status;
        this.responseTimeMs = responseTimeMs;
        this.requestPayload = requestPayload;
        this.responsePayload = responsePayload;
        this.errorMessage = errorMessage;
        this.failureType = failureType;
        this.failureReason = failureReason;
        this.suggestedAction = suggestedAction;
        this.retryCount = retryCount;
    }

    public static ExecutionLog pendingTimeout(
            InterfaceSystem interfaceSystem,
            LocalDateTime executedAt
    ) {
        return ExecutionLog.builder()
                .interfaceSystem(interfaceSystem)
                .executedAt(executedAt)
                .status(InterfaceStatus.FAILED)
                .responseTimeMs(0)
                .requestPayload(null)
                .responsePayload(null)
                .errorMessage("장시간 PENDING 상태가 지속되어 자동 실패 처리되었습니다.")
                .failureType(FailureType.TIMEOUT)
                .failureReason("30분 이상 대기 상태가 지속되었습니다.")
                .suggestedAction("외부 기관 응답 상태 및 배치 처리 여부를 확인하세요.")
                .retryCount(0)
                .build();
    }
}