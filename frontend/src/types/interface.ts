export type Status = "SUCCESS" | "FAILED" | "PENDING";

export type Protocol = "REST" | "SOAP" | "SFTP" | "MQ" | "BATCH";

export type FailureType =
    | "TIMEOUT"
    | "CONNECTION_ERROR"
    | "VALIDATION_ERROR"
    | "SYSTEM_ERROR"
    | "UNKNOWN"
    | "TIMEOUT";

export interface InterfaceItem {
    id: number;
    name: string;
    organization: string;
    protocol: Protocol;
    status: Status;
    ownerTeam: string;
    endpoint: string;
    description: string | null;
    lastExecutedAt: string | null;
    avgResponseMs: number;
    todayCount: number;
}

export interface CreateInterfaceRequest {
    organizationId: number;
    name: string;
    protocolType: Protocol;
    ownerTeam: string;
    endpoint: string;
    description: string;
}

export type UpdateInterfaceRequest = CreateInterfaceRequest;

export interface ExecutionLog {
    id: number;
    interfaceId: number;
    interfaceName: string;
    executedAt: string;
    status: Status;
    responseTimeMs: number;
    requestPayload: string | null;
    responsePayload: string | null;
    errorMessage: string | null;
    failureType: FailureType | null;
    failureReason: string | null;
    suggestedAction: string | null;
    retryCount: number;
}

export interface InterfaceDetail {
    interfaceInfo: InterfaceItem;
    recentLogs: ExecutionLog[];
}