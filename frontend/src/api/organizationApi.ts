import { apiRequest } from "./client";
import type {
    CreateOrganizationRequest,
    Organization,
    UpdateOrganizationRequest,
} from "../types/organization";

export async function getOrganizations() {
    return apiRequest<Organization[]>("/api/organizations");
}

export async function getOrganization(organizationId: number) {
    return apiRequest<Organization>(`/api/organizations/${organizationId}`);
}

export async function createOrganization(request: CreateOrganizationRequest) {
    return apiRequest<Organization>("/api/organizations", {
        method: "POST",
        body: JSON.stringify(request),
    });
}

export async function updateOrganization(
    organizationId: number,
    request: UpdateOrganizationRequest
) {
    return apiRequest<Organization>(`/api/organizations/${organizationId}`, {
        method: "PUT",
        body: JSON.stringify(request),
    });
}

export async function deleteOrganization(organizationId: number) {
    return apiRequest<void>(`/api/organizations/${organizationId}`, {
        method: "DELETE",
    });
}