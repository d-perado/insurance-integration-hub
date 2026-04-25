package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.domain.ProtocolType;
import com.insurance.integrationhub.dto.interfaces.CreateInterfaceSystemRequest;
import com.insurance.integrationhub.dto.interfaces.InterfaceDetailResponse;
import com.insurance.integrationhub.dto.interfaces.InterfaceResponse;
import com.insurance.integrationhub.dto.interfaces.UpdateInterfaceSystemRequest;
import com.insurance.integrationhub.service.InterfaceSystemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interfaces")
@RequiredArgsConstructor
public class InterfaceSystemController {

    private final InterfaceSystemService interfaceService;

    @PostMapping
    public ApiResponse<InterfaceResponse> createInterface(
            @Valid @RequestBody CreateInterfaceSystemRequest request
    ) {
        InterfaceResponse response = interfaceService.createInterfaceSystem(request);
        return ApiResponse.success(response, "인터페이스 등록이 완료되었습니다.");
    }

    @GetMapping
    public ApiResponse<List<InterfaceResponse>> getInterfaces(
            @RequestParam(required = false) InterfaceStatus status,
            @RequestParam(required = false) ProtocolType protocolType,
            @RequestParam(required = false) String keyword
    ) {
        List<InterfaceResponse> response = interfaceService.getInterfaces(
                status,
                protocolType,
                keyword
        );

        return ApiResponse.success(response);
    }

    @GetMapping("/{interfaceId}")
    public ApiResponse<InterfaceDetailResponse> getInterface(
            @PathVariable Long interfaceId
    ) {
        InterfaceDetailResponse response = interfaceService.getInterface(interfaceId);
        return ApiResponse.success(response);
    }

    @PutMapping("/{interfaceId}")
    public ApiResponse<InterfaceResponse> updateInterface(
            @PathVariable Long interfaceId,
            @Valid @RequestBody UpdateInterfaceSystemRequest request
    ) {
        InterfaceResponse response =
                interfaceService.updateInterfaceSystem(interfaceId, request);

        return ApiResponse.success(response, "인터페이스 정보 수정이 완료되었습니다.");
    }

    @PostMapping("/{interfaceId}/retry")
    public ApiResponse<InterfaceResponse> retryInterface(
            @PathVariable Long interfaceId
    ) {
        InterfaceResponse response = interfaceService.retryInterface(interfaceId);
        return ApiResponse.success(response, "인터페이스 재실행이 완료되었습니다.");
    }
}