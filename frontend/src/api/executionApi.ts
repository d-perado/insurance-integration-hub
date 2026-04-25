import { apiRequest } from "./client";
import type { ExecutionLog, FailureType, Status } from "../types/interface";

interface GetExecutionLogsParams {
    interfaceId?: number;
    status?: Status;
    failureType?: FailureType;
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

    const query = searchParams.toString();

    return apiRequest<ExecutionLog[]>(
        query ? `/api/executions?${query}` : "/api/executions"
    );
}

export async function getExecutionLog(logId: number) {
    return apiRequest<ExecutionLog>(`/api/executions/${logId}`);
}