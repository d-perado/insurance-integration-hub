package com.insurance.integrationhub.dto.interfaces;

import com.insurance.integrationhub.domain.ProtocolType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateInterfaceSystemRequest(
        @NotNull(message = "외부 기관 ID는 필수입니다.")
        Long organizationId,

        @NotBlank(message = "인터페이스명은 필수입니다.")
        String name,

        @NotNull(message = "프로토콜 타입은 필수입니다.")
        ProtocolType protocolType,

        @NotBlank(message = "담당팀은 필수입니다.")
        String ownerTeam,

        @NotBlank(message = "엔드포인트는 필수입니다.")
        String endpoint,

        String description
) {
}