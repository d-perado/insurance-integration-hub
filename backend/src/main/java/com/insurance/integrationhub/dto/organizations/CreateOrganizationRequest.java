package com.insurance.integrationhub.dto.organizations;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record CreateOrganizationRequest(
        @NotBlank(message = "기관명은 필수입니다.")
        String name,

        @NotBlank(message = "담당자명은 필수입니다.")
        String managerName,

        @NotBlank(message = "담당자 이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이어야 합니다.")
        String managerEmail
) {
}