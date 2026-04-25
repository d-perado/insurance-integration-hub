export type Status = "SUCCESS" | "FAILED" | "PENDING";
export type Protocol = "REST" | "SOAP" | "SFTP" | "MQ" | "BATCH";

export interface InterfaceItem {
    id: number;
    name: string;
    organization: string;
    protocol: Protocol;
    status: Status;
    ownerTeam: string;
    lastExecutedAt: string;
    avgResponseMs: number;
    todayCount: number;
    description: string;
}

export interface ExecutionLog {
    id: number;
    interfaceId: number;
    executedAt: string;
    status: Status;
    responseTimeMs: number;
    requestPayload: string;
    responsePayload: string;
    errorMessage?: string;
}