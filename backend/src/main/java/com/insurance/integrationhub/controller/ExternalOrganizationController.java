package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.dto.organizations.CreateOrganizationRequest;
import com.insurance.integrationhub.dto.organizations.OrganizationResponse;
import com.insurance.integrationhub.dto.organizations.UpdateOrganizationRequest;
import com.insurance.integrationhub.service.ExternalOrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Organization", description = "외부 기관 관리 API")
@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class ExternalOrganizationController {

    private final ExternalOrganizationService externalOrganizationService;

    @Operation(summary = "외부 기관 등록", description = "인터페이스와 연계할 외부 기관 정보를 등록합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {
        OrganizationResponse response =
                externalOrganizationService.createOrganization(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "외부 기관 등록이 완료되었습니다."));
    }

    @Operation(summary = "외부 기관 목록 조회", description = "등록된 외부 기관 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getOrganizations() {
        List<OrganizationResponse> response =
                externalOrganizationService.getOrganizations();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "외부 기관 상세 조회", description = "외부 기관 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(
            @Parameter(description = "외부 기관 ID", example = "1")
            @PathVariable Long organizationId
    ) {
        OrganizationResponse response =
                externalOrganizationService.getOrganization(organizationId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @Operation(summary = "외부 기관 수정", description = "등록된 외부 기관 정보를 수정합니다.")
    @PutMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @Parameter(description = "외부 기관 ID", example = "1")
            @PathVariable Long organizationId,

            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        OrganizationResponse response =
                externalOrganizationService.updateOrganization(organizationId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "외부 기관 정보 수정이 완료되었습니다."));
    }

    @Operation(summary = "외부 기관 삭제", description = "등록된 외부 기관 정보를 삭제합니다.")
    @DeleteMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(
            @Parameter(description = "외부 기관 ID", example = "1")
            @PathVariable Long organizationId
    ) {
        externalOrganizationService.deleteOrganization(organizationId);

        return ResponseEntity.ok(ApiResponse.success(null, "외부 기관 삭제가 완료되었습니다."));
    }
}