export interface Organization {
    id: number;
    name: string;
    managerName: string;
    managerEmail: string;
    createdAt: string;
}

export interface CreateOrganizationRequest {
    name: string;
    managerName: string;
    managerEmail: string;
}

export type UpdateOrganizationRequest = CreateOrganizationRequest;