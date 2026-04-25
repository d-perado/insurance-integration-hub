package com.insurance.integrationhub.controller;

import com.insurance.integrationhub.common.response.ApiResponse;
import com.insurance.integrationhub.domain.InterfaceStatus;
import com.insurance.integrationhub.domain.ProtocolType;
import com.insurance.integrationhub.dto.interfaces.InterfaceDetailResponse;
import com.insurance.integrationhub.dto.interfaces.InterfaceResponse;
import com.insurance.integrationhub.service.InterfaceSystemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/interfaces")
@RequiredArgsConstructor
public class InterfaceSystemController {

    private final InterfaceSystemService interfaceService;

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

    @PostMapping("/{interfaceId}/retry")
    public ApiResponse<InterfaceResponse> retryInterface(
            @PathVariable Long interfaceId
    ) {
        InterfaceResponse response = interfaceService.retryInterface(interfaceId);
        return ApiResponse.success(response, "인터페이스 재실행이 완료되었습니다.");
    }
}