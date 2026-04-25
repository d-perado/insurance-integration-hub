import { useEffect, useState } from "react";
import StatusBadge from "../components/ui/StatusBadge";
import type { ExecutionLog, FailureType, Status } from "../types/interface";
import { getExecutionLog, getExecutionLogs } from "../api/executionApi";

export default function ExecutionHistoryPage() {
    const [logs, setLogs] = useState<ExecutionLog[]>([]);
    const [selectedLog, setSelectedLog] = useState<ExecutionLog | null>(null);
    const [statusFilter, setStatusFilter] = useState<"ALL" | Status>("ALL");
    const [failureTypeFilter, setFailureTypeFilter] =
        useState<"ALL" | FailureType>("ALL");
    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const loadLogs = async () => {
        setIsLoading(true);
        setErrorMessage("");

        try {
            const data = await getExecutionLogs({
                status: statusFilter === "ALL" ? undefined : statusFilter,
                failureType:
                    failureTypeFilter === "ALL"
                        ? undefined
                        : failureTypeFilter,
            });

            setLogs(data);
        } catch (error) {
            setErrorMessage(
                error instanceof Error
                    ? error.message
                    : "실행 이력 조회 중 오류가 발생했습니다."
            );
        } finally {
            setIsLoading(false);
        }
    };

    const openLogDetail = async (logId: number) => {
        try {
            const data = await getExecutionLog(logId);
            setSelectedLog(data);
        } catch (error) {
            setErrorMessage(
                error instanceof Error
                    ? error.message
                    : "실행 로그 상세 조회 중 오류가 발생했습니다."
            );
        }
    };

    useEffect(() => {
        loadLogs();
    }, [statusFilter, failureTypeFilter]);

    return (
        <>
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                <div className="flex flex-col gap-4 2xl:flex-row 2xl:items-center 2xl:justify-between">
                    <div>
                        <h3 className="text-lg font-bold">실행 이력</h3>

                        <p className="mt-1 text-sm text-slate-500">
                            인터페이스 실행 결과와 실패 원인을 시간순으로 확인합니다.
                        </p>
                    </div>

                    <div className="grid gap-2 md:grid-cols-2 xl:grid-cols-3 2xl:flex 2xl:flex-row">
                        <select
                            value={statusFilter}
                            onChange={(e) =>
                                setStatusFilter(
                                    e.target.value as "ALL" | Status
                                )
                            }
                            className="h-10 rounded-xl border border-slate-200 bg-white px-3 text-sm"
                        >
                            <option value="ALL">전체 상태</option>
                            <option value="SUCCESS">성공</option>
                            <option value="FAILED">실패</option>
                            <option value="PENDING">대기</option>
                        </select>

                        <select
                            value={failureTypeFilter}
                            onChange={(e) =>
                                setFailureTypeFilter(
                                    e.target.value as "ALL" | FailureType
                                )
                            }
                            className="h-10 rounded-xl border border-slate-200 bg-white px-3 text-sm"
                        >
                            <option value="ALL">전체 실패 유형</option>
                            <option value="TIMEOUT">TIMEOUT</option>
                            <option value="CONNECTION_ERROR">
                                CONNECTION_ERROR
                            </option>
                            <option value="VALIDATION_ERROR">
                                VALIDATION_ERROR
                            </option>
                            <option value="SYSTEM_ERROR">
                                SYSTEM_ERROR
                            </option>
                            <option value="UNKNOWN">UNKNOWN</option>
                        </select>

                        <button
                            onClick={loadLogs}
                            className="h-10 rounded-xl border border-slate-200 px-4 text-sm font-semibold transition hover:bg-slate-50"
                        >
                            조회
                        </button>
                    </div>
                </div>

                {errorMessage && (
                    <div className="mt-5 rounded-2xl border border-red-100 bg-red-50 px-5 py-4 text-sm font-medium text-red-700">
                        {errorMessage}
                    </div>
                )}

                <div className="mt-5 overflow-x-auto rounded-2xl border border-slate-200">
                    <table className="min-w-[1000px] w-full text-sm">
                        <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                        <tr>
                            <th className="px-4 py-3">실행 시간</th>
                            <th className="px-4 py-3">인터페이스</th>
                            <th className="px-4 py-3">상태</th>
                            <th className="px-4 py-3">응답시간</th>
                            <th className="px-4 py-3">실패 유형</th>
                            <th className="px-4 py-3">메시지</th>
                            <th className="px-4 py-3 text-right">작업</th>
                        </tr>
                        </thead>

                        <tbody className="divide-y divide-slate-100">
                        {isLoading ? (
                            <tr>
                                <td
                                    colSpan={7}
                                    className="px-4 py-10 text-center text-slate-500"
                                >
                                    데이터를 불러오는 중입니다.
                                </td>
                            </tr>
                        ) : logs.length === 0 ? (
                            <tr>
                                <td
                                    colSpan={7}
                                    className="px-4 py-10 text-center text-slate-500"
                                >
                                    조회된 실행 이력이 없습니다.
                                </td>
                            </tr>
                        ) : (
                            logs.map((log) => (
                                <tr
                                    key={log.id}
                                    className="hover:bg-slate-50"
                                >
                                    <td className="px-4 py-4 text-slate-600">
                                        {log.executedAt}
                                    </td>

                                    <td className="px-4 py-4 font-semibold">
                                        {log.interfaceName}
                                    </td>

                                    <td className="px-4 py-4">
                                        <StatusBadge status={log.status} />
                                    </td>

                                    <td className="px-4 py-4">
                                        {log.responseTimeMs}ms
                                    </td>

                                    <td className="px-4 py-4 text-slate-600">
                                        {log.failureType ?? "-"}
                                    </td>

                                    <td className="px-4 py-4 text-slate-600">
                                        {log.errorMessage ?? "정상 처리"}
                                    </td>

                                    <td className="px-4 py-4 text-right">
                                        <button
                                            onClick={() =>
                                                openLogDetail(log.id)
                                            }
                                            className="rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold transition hover:bg-slate-100"
                                        >
                                            상세
                                        </button>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {selectedLog && (
                <LogDetailModal
                    log={selectedLog}
                    onClose={() => setSelectedLog(null)}
                />
            )}
        </>
    );
}

function LogDetailModal({
                            log,
                            onClose,
                        }: {
    log: ExecutionLog;
    onClose: () => void;
}) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 px-4">
            <div className="max-h-[90vh] w-full max-w-4xl overflow-y-auto rounded-3xl bg-white shadow-2xl">
                <div className="flex flex-col gap-4 border-b border-slate-100 px-7 py-6 2xl:flex-row 2xl:items-start 2xl:justify-between">
                    <div>
                        <StatusBadge status={log.status} />

                        <h3 className="mt-3 text-2xl font-bold">
                            {log.interfaceName}
                        </h3>

                        <p className="mt-2 text-sm text-slate-500">
                            {log.executedAt} · 응답시간 {log.responseTimeMs}ms
                            · 재시도 {log.retryCount}회
                        </p>
                    </div>

                    <button
                        onClick={onClose}
                        className="w-fit rounded-xl border border-slate-200 px-4 py-2 text-sm font-semibold transition hover:bg-slate-50"
                    >
                        닫기
                    </button>
                </div>

                <div className="space-y-5 px-7 py-6">
                    <div className="grid gap-4 2xl:grid-cols-3">
                        <InfoBox
                            title="실패 유형"
                            value={log.failureType ?? "-"}
                        />

                        <InfoBox
                            title="실패 원인"
                            value={log.failureReason ?? "-"}
                        />

                        <InfoBox
                            title="조치 방안"
                            value={log.suggestedAction ?? "-"}
                        />
                    </div>

                    {log.errorMessage && (
                        <div className="rounded-2xl bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                            {log.errorMessage}
                        </div>
                    )}

                    <div className="grid gap-4 2xl:grid-cols-2">
                        <PayloadBox
                            title="Request Payload"
                            value={log.requestPayload ?? "-"}
                        />

                        <PayloadBox
                            title="Response Payload"
                            value={log.responsePayload ?? "-"}
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}

function InfoBox({
                     title,
                     value,
                 }: {
    title: string;
    value: string;
}) {
    return (
        <div className="rounded-2xl bg-slate-50 px-4 py-3 text-sm">
            <p className="font-bold text-slate-700">{title}</p>
            <p className="mt-1 text-slate-600">{value}</p>
        </div>
    );
}

function PayloadBox({
                        title,
                        value,
                    }: {
    title: string;
    value: string;
}) {
    return (
        <div>
            <p className="mb-2 text-xs font-bold text-slate-500">
                {title}
            </p>

            <pre className="max-h-80 overflow-auto rounded-2xl bg-slate-950 p-4 text-xs leading-relaxed text-slate-100">
                {value}
            </pre>
        </div>
    );
}