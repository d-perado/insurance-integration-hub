package com.insurance.integrationhub.scheduler;

import com.insurance.integrationhub.domain.ExecutionLog;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.domain.InterfaceSystem;
import com.insurance.integrationhub.repository.ExecutionLogRepository;
import com.insurance.integrationhub.repository.InterfaceSystemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InterfaceMonitoringScheduler {

    private static final int PENDING_TIMEOUT_MINUTES = 30;

    private final InterfaceSystemRepository interfaceSystemRepository;
    private final ExecutionLogRepository executionLogRepository;

    @Transactional
    @Scheduled(cron = "0 */5 * * * *")
    public void checkPendingInterfaces() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusMinutes(PENDING_TIMEOUT_MINUTES);

        List<InterfaceSystem> timeoutInterfaces =
                interfaceSystemRepository.findByStatusAndLastExecutedAtBefore(
                        InterfaceStatus.PENDING,
                        threshold
                );

        if (timeoutInterfaces.isEmpty()) {
            log.info("장기 대기 인터페이스 없음");
            return;
        }

        for (InterfaceSystem interfaceSystem : timeoutInterfaces) {
            interfaceSystem.markAsFailed(now);

            ExecutionLog log = ExecutionLog.pendingTimeout(
                    interfaceSystem,
                    now
            );

            executionLogRepository.save(log);
        }

        log.warn(
                "장기 대기 인터페이스 {}건 자동 실패 처리 완료",
                timeoutInterfaces.size()
        );
    }
}