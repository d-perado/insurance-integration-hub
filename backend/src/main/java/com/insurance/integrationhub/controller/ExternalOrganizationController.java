package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.dto.organizations.CreateOrganizationRequest;
import com.insurance.integrationhub.dto.organizations.OrganizationResponse;
import com.insurance.integrationhub.dto.organizations.UpdateOrganizationRequest;
import com.insurance.integrationhub.service.ExternalOrganizationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizations")
@RequiredArgsConstructor
public class ExternalOrganizationController {

    private final ExternalOrganizationService externalOrganizationService;

    @PostMapping
    public ResponseEntity<ApiResponse<OrganizationResponse>> createOrganization(
            @Valid @RequestBody CreateOrganizationRequest request
    ) {
        OrganizationResponse response =
                externalOrganizationService.createOrganization(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, "외부 기관 등록이 완료되었습니다."));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<OrganizationResponse>>> getOrganizations() {
        List<OrganizationResponse> response =
                externalOrganizationService.getOrganizations();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> getOrganization(
            @PathVariable Long organizationId
    ) {
        OrganizationResponse response =
                externalOrganizationService.getOrganization(organizationId);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<OrganizationResponse>> updateOrganization(
            @PathVariable Long organizationId,
            @Valid @RequestBody UpdateOrganizationRequest request
    ) {
        OrganizationResponse response =
                externalOrganizationService.updateOrganization(organizationId, request);

        return ResponseEntity.ok(ApiResponse.success(response, "외부 기관 정보 수정이 완료되었습니다."));
    }

    @DeleteMapping("/{organizationId}")
    public ResponseEntity<ApiResponse<Void>> deleteOrganization(
            @PathVariable Long organizationId
    ) {
        externalOrganizationService.deleteOrganization(organizationId);

        return ResponseEntity.ok(ApiResponse.success(null, "외부 기관 삭제가 완료되었습니다."));
    }
}