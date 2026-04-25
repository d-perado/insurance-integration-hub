import { apiRequest } from "./client";
import type { PageResponse } from "../types/api";
import type { ExecutionLog, FailureType, Status } from "../types/interface";

interface GetExecutionLogsParams {
    interfaceId?: number;
    status?: Status;
    failureType?: FailureType;
    page?: number;
    size?: number;
}

export async function getExecutionLogs(params: GetExecutionLogsParams = {}) {
    const searchParams = new URLSearchParams();

    if (params.interfaceId) {
        searchParams.append("interfaceId", String(params.interfaceId));
    }

    if (params.status) {
        searchParams.append("status", params.status);
    }

    if (params.failureType) {
        searchParams.append("failureType", params.failureType);
    }

    searchParams.append("page", String(params.page ?? 0));
    searchParams.append("size", String(params.size ?? 10));

    return apiRequest<PageResponse<ExecutionLog>>(
        `/api/executions?${searchParams.toString()}`
    );
}

export async function getExecutionLog(logId: number) {
    return apiRequest<ExecutionLog>(`/api/executions/${logId}`);
}