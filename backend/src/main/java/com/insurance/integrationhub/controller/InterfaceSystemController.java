package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.domain.ProtocolType;
import com.insurance.integrationhub.dto.interfaces.*;
import com.insurance.integrationhub.service.InterfaceSystemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Interface", description = "인터페이스 관리 API")
@RestController
@RequestMapping("/api/interfaces")
@RequiredArgsConstructor
public class InterfaceSystemController {

    private final InterfaceSystemService interfaceService;

    @Operation(summary = "인터페이스 등록", description = "외부 기관과 연계할 인터페이스 정보를 등록합니다.")
    @PostMapping
    public ApiResponse<InterfaceResponse> createInterface(
            @Valid @RequestBody CreateInterfaceSystemRequest request
    ) {
        InterfaceResponse response = interfaceService.createInterfaceSystem(request);
        return ApiResponse.success(response, "인터페이스 등록이 완료되었습니다.");
    }

    @Operation(summary = "인터페이스 목록 조회", description = "상태, 프로토콜, 키워드 조건으로 인터페이스 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<List<InterfaceResponse>> getInterfaces(
            @Parameter(description = "인터페이스 상태")
            @RequestParam(required = false) InterfaceStatus status,

            @Parameter(description = "프로토콜 타입")
            @RequestParam(required = false) ProtocolType protocolType,

            @Parameter(description = "검색 키워드")
            @RequestParam(required = false) String keyword
    ) {
        List<InterfaceResponse> response = interfaceService.getInterfaces(
                status,
                protocolType,
                keyword
        );

        return ApiResponse.success(response);
    }

    @Operation(summary = "인터페이스 상세 조회", description = "인터페이스 상세 정보와 최근 실행 로그를 조회합니다.")
    @GetMapping("/{interfaceId}")
    public ApiResponse<InterfaceDetailResponse> getInterface(
            @Parameter(description = "인터페이스 ID", example = "1")
            @PathVariable Long interfaceId
    ) {
        InterfaceDetailResponse response = interfaceService.getInterface(interfaceId);
        return ApiResponse.success(response);
    }

    @Operation(summary = "인터페이스 수정", description = "등록된 인터페이스 정보를 수정합니다.")
    @PutMapping("/{interfaceId}")
    public ApiResponse<InterfaceResponse> updateInterface(
            @Parameter(description = "인터페이스 ID", example = "1")
            @PathVariable Long interfaceId,

            @Valid @RequestBody UpdateInterfaceSystemRequest request
    ) {
        InterfaceResponse response =
                interfaceService.updateInterfaceSystem(interfaceId, request);

        return ApiResponse.success(response, "인터페이스 정보 수정이 완료되었습니다.");
    }

    @Operation(summary = "인터페이스 재실행", description = "실패 또는 대기 상태의 인터페이스를 재실행합니다.")
    @PostMapping("/{interfaceId}/retry")
    public ApiResponse<InterfaceResponse> retryInterface(
            @Parameter(description = "인터페이스 ID", example = "1")
            @PathVariable Long interfaceId
    ) {
        InterfaceResponse response = interfaceService.retryInterface(interfaceId);
        return ApiResponse.success(response, "인터페이스 재실행이 완료되었습니다.");
    }

    @Operation(
            summary = "운영 상태 조회",
            description = "전체 인터페이스 중 FAILED 상태 존재 여부를 조회합니다."
    )
    @GetMapping("/operation-status")
    public ApiResponse<OperationStatusResponse> getOperationStatus() {
        OperationStatusResponse response = interfaceService.getOperationStatus();

        return ApiResponse.success(response);
    }
}