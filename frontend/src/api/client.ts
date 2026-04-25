import type { ApiResponse } from "../types/api";

const BASE_URL = "http://localhost:8080";

export async function apiRequest<T>(
    path: string,
    options?: RequestInit
): Promise<T> {
    const response = await fetch(`${BASE_URL}${path}`, {
        headers: {
            "Content-Type": "application/json",
            ...(options?.headers ?? {}),
        },
        ...options,
    });

    const body: ApiResponse<T> = await response.json();

    if (!response.ok || !body.success) {
        throw new Error(body.message || "API 요청 처리 중 오류가 발생했습니다.");
    }

    return body.data;
}