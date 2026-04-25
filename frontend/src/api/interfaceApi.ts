import { apiRequest } from "./client";
import type {
    CreateInterfaceRequest,
    InterfaceDetail,
    InterfaceItem,
    Protocol,
    Status,
    UpdateInterfaceRequest,
} from "../types/interface";

interface GetInterfacesParams {
    status?: Status;
    protocolType?: Protocol;
    keyword?: string;
}

export async function getInterfaces(params: GetInterfacesParams = {}) {
    const searchParams = new URLSearchParams();

    if (params.status) {
        searchParams.append("status", params.status);
    }

    if (params.protocolType) {
        searchParams.append("protocolType", params.protocolType);
    }

    if (params.keyword) {
        searchParams.append("keyword", params.keyword);
    }

    const query = searchParams.toString();

    return apiRequest<InterfaceItem[]>(
        query ? `/api/interfaces?${query}` : "/api/interfaces"
    );
}

export async function getInterface(interfaceId: number) {
    return apiRequest<InterfaceDetail>(`/api/interfaces/${interfaceId}`);
}

export async function createInterface(request: CreateInterfaceRequest) {
    return apiRequest<InterfaceItem>("/api/interfaces", {
        method: "POST",
        body: JSON.stringify(request),
    });
}

export async function updateInterface(
    interfaceId: number,
    request: UpdateInterfaceRequest
) {
    return apiRequest<InterfaceItem>(`/api/interfaces/${interfaceId}`, {
        method: "PUT",
        body: JSON.stringify(request),
    });
}

export async function retryInterface(interfaceId: number) {
    return apiRequest<InterfaceItem>(`/api/interfaces/${interfaceId}/retry`, {
        method: "POST",
    });
}