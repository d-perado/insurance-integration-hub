import { useEffect, useMemo, useState } from "react";
import type {
    CreateInterfaceRequest,
    ExecutionLog,
    InterfaceItem,
    Protocol,
    Status,
} from "../types/interface";
import type { Organization } from "../types/organization";
import ProtocolBadge from "../components/ui/ProtocolBadge";
import StatusBadge from "../components/ui/StatusBadge";
import {
    createInterface,
    getInterface,
    getInterfaces,
    retryInterface,
    updateInterface,
} from "../api/interfaceApi";
import { getOrganizations } from "../api/organizationApi";

const emptyForm: CreateInterfaceRequest = {
    organizationId: 0,
    name: "",
    protocolType: "REST",
    ownerTeam: "",
    endpoint: "",
    description: "",
};

export default function DashboardPage() {
    const [interfaces, setInterfaces] = useState<InterfaceItem[]>([]);
    const [organizations, setOrganizations] = useState<Organization[]>([]);
    const [selected, setSelected] = useState<InterfaceItem | null>(null);
    const [recentLogs, setRecentLogs] = useState<ExecutionLog[]>([]);

    const [search, setSearch] = useState("");
    const [statusFilter, setStatusFilter] = useState<"ALL" | Status>("ALL");
    const [protocolFilter, setProtocolFilter] = useState<"ALL" | Protocol>("ALL");

    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingItem, setEditingItem] = useState<InterfaceItem | null>(null);
    const [form, setForm] = useState<CreateInterfaceRequest>(emptyForm);

    const loadInterfaces = async () => {
        setIsLoading(true);
        setErrorMessage("");

        try {
            const data = await getInterfaces({
                status: statusFilter === "ALL" ? undefined : statusFilter,
                protocolType: protocolFilter === "ALL" ? undefined : protocolFilter,
                keyword: search.trim() || undefined,
            });

            setInterfaces(data);
        } catch (error) {
            setErrorMessage(
                error instanceof Error ? error.message : "인터페이스 목록 조회 중 오류가 발생했습니다."
            );
        } finally {
            setIsLoading(false);
        }
    };

    const loadOrganizations = async () => {
        try {
            const data = await getOrganizations();
            setOrganizations(data);
        } catch {
            setOrganizations([]);
        }
    };

    useEffect(() => {
        loadOrganizations();
    }, []);

    useEffect(() => {
        loadInterfaces();
    }, [statusFilter, protocolFilter]);

    const summary = useMemo(() => {
        const total = interfaces.length;
        const success = interfaces.filter((item) => item.status === "SUCCESS").length;
        const failed = interfaces.filter((item) => item.status === "FAILED").length;
        const pending = interfaces.filter((item) => item.status === "PENDING").length;
        const avgResponse =
            total === 0
                ? 0
                : Math.round(
                    interfaces.reduce((sum, item) => sum + item.avgResponseMs, 0) / total
                );

        return {
            total,
            failed,
            pending,
            successRate: total === 0 ? 0 : Math.round((success / total) * 100),
            avgResponse,
        };
    }, [interfaces]);

    const openDetail = async (item: InterfaceItem) => {
        setSelected(item);
        setRecentLogs([]);

        try {
            const detail = await getInterface(item.id);
            setSelected(detail.interfaceInfo);
            setRecentLogs(detail.recentLogs);
        } catch (error) {
            setErrorMessage(
                error instanceof Error ? error.message : "상세 조회 중 오류가 발생했습니다."
            );
        }
    };

    const handleRetry = async (id: number) => {
        try {
            await retryInterface(id);
            await loadInterfaces();

            if (selected?.id === id) {
                const detail = await getInterface(id);
                setSelected(detail.interfaceInfo);
                setRecentLogs(detail.recentLogs);
            }
        } catch (error) {
            setErrorMessage(
                error instanceof Error ? error.message : "재실행 중 오류가 발생했습니다."
            );
        }
    };

    const openCreateForm = () => {
        setEditingItem(null);
        setForm({
            ...emptyForm,
            organizationId: organizations[0]?.id ?? 0,
        });
        setIsFormOpen(true);
    };

    const openEditForm = (item: InterfaceItem) => {
        const organization = organizations.find((org) => org.name === item.organization);

        setEditingItem(item);
        setForm({
            organizationId: organization?.id ?? 0,
            name: item.name,
            protocolType: item.protocol,
            ownerTeam: item.ownerTeam,
            endpoint: item.endpoint,
            description: item.description ?? "",
        });
        setIsFormOpen(true);
    };

    const submitForm = async () => {
        try {
            if (editingItem) {
                await updateInterface(editingItem.id, form);
            } else {
                await createInterface(form);
            }

            setIsFormOpen(false);
            setEditingItem(null);
            setForm(emptyForm);
            await loadInterfaces();
        } catch (error) {
            setErrorMessage(
                error instanceof Error ? error.message : "인터페이스 저장 중 오류가 발생했습니다."
            );
        }
    };

    return (
        <>
            <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
                <MetricCard title="전체 인터페이스" value={`${summary.total}개`} />
                <MetricCard title="성공률" value={`${summary.successRate}%`} accent />
                <MetricCard title="실패" value={`${summary.failed}건`} danger />
                <MetricCard title="대기" value={`${summary.pending}건`} />
                <MetricCard title="평균 응답시간" value={`${summary.avgResponse}ms`} />
            </div>

            {errorMessage && (
                <div className="mt-5 rounded-2xl border border-red-100 bg-red-50 px-5 py-4 text-sm font-medium text-red-700">
                    {errorMessage}
                </div>
            )}

            <div className="mt-6 grid gap-6 xl:grid-cols-[1fr_360px]">
                <section className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                    <div className="flex flex-col gap-4 border-b border-slate-100 pb-5 xl:flex-row xl:items-center xl:justify-between">
                        <div>
                            <h3 className="text-lg font-bold">인터페이스 목록</h3>
                            <p className="mt-1 text-sm text-slate-500">
                                기관 연계 상태를 확인하고 실패 건은 상세 로그 확인 후 재실행할 수 있습니다.
                            </p>
                        </div>

                        <div className="flex flex-col gap-2 md:flex-row">
                            <input
                                value={search}
                                onChange={(e) => setSearch(e.target.value)}
                                onKeyDown={(e) => {
                                    if (e.key === "Enter") {
                                        loadInterfaces();
                                    }
                                }}
                                placeholder="기관명, 인터페이스명, 담당팀 검색"
                                className="h-10 rounded-xl border border-slate-200 bg-slate-50 px-3 text-sm outline-none transition focus:border-blue-500 focus:bg-white md:w-72"
                            />

                            <select
                                value={statusFilter}
                                onChange={(e) => setStatusFilter(e.target.value as "ALL" | Status)}
                                className="h-10 rounded-xl border border-slate-200 bg-white px-3 text-sm"
                            >
                                <option value="ALL">전체 상태</option>
                                <option value="SUCCESS">성공</option>
                                <option value="FAILED">실패</option>
                                <option value="PENDING">대기</option>
                            </select>

                            <select
                                value={protocolFilter}
                                onChange={(e) => setProtocolFilter(e.target.value as "ALL" | Protocol)}
                                className="h-10 rounded-xl border border-slate-200 bg-white px-3 text-sm"
                            >
                                <option value="ALL">전체 프로토콜</option>
                                <option value="REST">REST</option>
                                <option value="SOAP">SOAP</option>
                                <option value="SFTP">SFTP</option>
                                <option value="MQ">MQ</option>
                                <option value="BATCH">BATCH</option>
                            </select>

                            <button
                                onClick={loadInterfaces}
                                className="h-10 rounded-xl border border-slate-200 px-4 text-sm font-semibold transition hover:bg-slate-50"
                            >
                                조회
                            </button>

                            <button
                                onClick={openCreateForm}
                                className="h-10 rounded-xl bg-blue-600 px-4 text-sm font-bold text-white transition hover:bg-blue-700"
                            >
                                등록
                            </button>
                        </div>
                    </div>

                    <div className="mt-5 overflow-hidden rounded-2xl border border-slate-200">
                        <table className="w-full border-collapse text-sm">
                            <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                            <tr>
                                <th className="px-4 py-3">인터페이스</th>
                                <th className="px-4 py-3">기관</th>
                                <th className="px-4 py-3">프로토콜</th>
                                <th className="px-4 py-3">상태</th>
                                <th className="px-4 py-3">최근 실행</th>
                                <th className="px-4 py-3">응답</th>
                                <th className="px-4 py-3 text-right">작업</th>
                            </tr>
                            </thead>

                            <tbody className="divide-y divide-slate-100">
                            {isLoading ? (
                                <tr>
                                    <td colSpan={7} className="px-4 py-10 text-center text-slate-500">
                                        데이터를 불러오는 중입니다.
                                    </td>
                                </tr>
                            ) : interfaces.length === 0 ? (
                                <tr>
                                    <td colSpan={7} className="px-4 py-10 text-center text-slate-500">
                                        조회된 인터페이스가 없습니다.
                                    </td>
                                </tr>
                            ) : (
                                interfaces.map((item) => (
                                    <tr key={item.id} className="transition hover:bg-slate-50">
                                        <td className="px-4 py-4">
                                            <p className="font-semibold">{item.name}</p>
                                            <p className="mt-1 text-xs text-slate-500">{item.ownerTeam}</p>
                                        </td>
                                        <td className="px-4 py-4 text-slate-700">{item.organization}</td>
                                        <td className="px-4 py-4">
                                            <ProtocolBadge protocol={item.protocol} />
                                        </td>
                                        <td className="px-4 py-4">
                                            <StatusBadge status={item.status} />
                                        </td>
                                        <td className="px-4 py-4 text-slate-500">
                                            {item.lastExecutedAt ?? "-"}
                                        </td>
                                        <td className="px-4 py-4 font-medium">{item.avgResponseMs}ms</td>
                                        <td className="px-4 py-4">
                                            <div className="flex justify-end gap-2">
                                                <button
                                                    onClick={() => openDetail(item)}
                                                    className="rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold transition hover:bg-slate-100"
                                                >
                                                    상세
                                                </button>
                                                <button
                                                    onClick={() => openEditForm(item)}
                                                    className="rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold transition hover:bg-slate-100"
                                                >
                                                    수정
                                                </button>
                                                <button
                                                    onClick={() => handleRetry(item.id)}
                                                    className="rounded-xl bg-slate-900 px-3 py-2 text-xs font-semibold text-white transition hover:bg-slate-700"
                                                >
                                                    재실행
                                                </button>
                                            </div>
                                        </td>
                                    </tr>
                                ))
                            )}
                            </tbody>
                        </table>
                    </div>
                </section>

                <section className="space-y-6">
                    <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                        <h3 className="text-lg font-bold">장애 대응 현황</h3>
                        <p className="mt-1 text-sm text-slate-500">
                            실패 상태의 인터페이스를 우선 확인합니다.
                        </p>

                        <div className="mt-5 space-y-3">
                            {interfaces.filter((item) => item.status === "FAILED").length === 0 ? (
                                <div className="rounded-2xl border border-slate-200 px-4 py-8 text-center text-sm text-slate-500">
                                    확인 필요한 실패 인터페이스가 없습니다.
                                </div>
                            ) : (
                                interfaces
                                    .filter((item) => item.status === "FAILED")
                                    .map((item) => (
                                        <button
                                            key={item.id}
                                            onClick={() => openDetail(item)}
                                            className="w-full rounded-2xl border border-red-100 bg-red-50 p-4 text-left transition hover:bg-red-100"
                                        >
                                            <div className="flex items-center justify-between">
                                                <p className="font-semibold text-red-800">{item.name}</p>
                                                <span className="text-xs font-bold text-red-600">확인 필요</span>
                                            </div>
                                            <p className="mt-1 text-sm text-red-700">
                                                {item.organization} · {item.lastExecutedAt ?? "-"}
                                            </p>
                                        </button>
                                    ))
                            )}
                        </div>
                    </div>
                </section>
            </div>

            {selected && (
                <DetailModal
                    item={selected}
                    logs={recentLogs}
                    onClose={() => setSelected(null)}
                    onRetry={() => handleRetry(selected.id)}
                />
            )}

            {isFormOpen && (
                <InterfaceFormModal
                    title={editingItem ? "인터페이스 수정" : "인터페이스 등록"}
                    form={form}
                    organizations={organizations}
                    onChange={setForm}
                    onClose={() => setIsFormOpen(false)}
                    onSubmit={submitForm}
                />
            )}
        </>
    );
}

function MetricCard({
                        title,
                        value,
                        accent = false,
                        danger = false,
                    }: {
    title: string;
    value: string;
    accent?: boolean;
    danger?: boolean;
}) {
    return (
        <div className="rounded-3xl border border-slate-200 bg-white p-5 shadow-sm">
            <p className="text-sm font-medium text-slate-500">{title}</p>
            <p className={`mt-3 text-3xl font-bold ${danger ? "text-red-600" : accent ? "text-blue-600" : "text-slate-900"}`}>
                {value}
            </p>
        </div>
    );
}

function DetailModal({
                         item,
                         logs,
                         onClose,
                         onRetry,
                     }: {
    item: InterfaceItem;
    logs: ExecutionLog[];
    onClose: () => void;
    onRetry: () => void;
}) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 px-4">
            <div className="max-h-[90vh] w-full max-w-4xl overflow-y-auto rounded-3xl bg-white shadow-2xl">
                <div className="border-b border-slate-100 px-7 py-6">
                    <div className="flex items-start justify-between gap-5">
                        <div>
                            <div className="mb-3 flex items-center gap-2">
                                <StatusBadge status={item.status} />
                                <ProtocolBadge protocol={item.protocol} />
                            </div>
                            <h3 className="text-2xl font-bold">{item.name}</h3>
                            <p className="mt-2 text-sm text-slate-500">
                                {item.organization} · {item.ownerTeam}
                            </p>
                        </div>

                        <button
                            onClick={onClose}
                            className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-semibold transition hover:bg-slate-50"
                        >
                            닫기
                        </button>
                    </div>
                </div>

                <div className="space-y-6 px-7 py-6">
                    <div className="rounded-2xl bg-slate-50 p-5">
                        <p className="text-sm font-semibold text-slate-700">설명</p>
                        <p className="mt-2 text-sm text-slate-600">
                            {item.description || "등록된 설명이 없습니다."}
                        </p>
                    </div>

                    <div className="rounded-2xl border border-slate-200 p-5">
                        <div className="flex items-center justify-between">
                            <div>
                                <h4 className="font-bold">장애 대응 액션</h4>
                                <p className="mt-1 text-sm text-slate-500">
                                    실패 로그 확인 후 재실행하여 상태를 복구합니다.
                                </p>
                            </div>

                            <button
                                onClick={onRetry}
                                className="rounded-xl bg-blue-600 px-5 py-3 text-sm font-bold text-white transition hover:bg-blue-700"
                            >
                                인터페이스 재실행
                            </button>
                        </div>
                    </div>

                    <div>
                        <h4 className="mb-4 font-bold">최근 실행 로그</h4>

                        {logs.length === 0 ? (
                            <div className="rounded-2xl border border-slate-200 px-4 py-10 text-center text-sm text-slate-500">
                                최근 실행 로그가 없습니다.
                            </div>
                        ) : (
                            <div className="space-y-4">
                                {logs.map((log) => (
                                    <div key={log.id} className="rounded-2xl border border-slate-200 p-5">
                                        <div className="mb-4 flex items-center justify-between">
                                            <div>
                                                <p className="font-semibold">{log.executedAt}</p>
                                                <p className="mt-1 text-sm text-slate-500">
                                                    응답시간 {log.responseTimeMs}ms · 재시도 {log.retryCount}회
                                                </p>
                                            </div>
                                            <StatusBadge status={log.status} />
                                        </div>

                                        {log.errorMessage && (
                                            <div className="mb-4 rounded-xl bg-red-50 px-4 py-3 text-sm font-medium text-red-700">
                                                {log.errorMessage}
                                            </div>
                                        )}

                                        {(log.failureReason || log.suggestedAction) && (
                                            <div className="mb-4 grid gap-3 md:grid-cols-2">
                                                <InfoBox title="실패 원인" value={log.failureReason ?? "-"} />
                                                <InfoBox title="조치 방안" value={log.suggestedAction ?? "-"} />
                                            </div>
                                        )}

                                        <div className="grid gap-4 md:grid-cols-2">
                                            <PayloadBox title="Request Payload" value={log.requestPayload ?? "-"} />
                                            <PayloadBox title="Response Payload" value={log.responsePayload ?? "-"} />
                                        </div>
                                    </div>
                                ))}
                            </div>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

function InterfaceFormModal({
                                title,
                                form,
                                organizations,
                                onChange,
                                onClose,
                                onSubmit,
                            }: {
    title: string;
    form: CreateInterfaceRequest;
    organizations: Organization[];
    onChange: (form: CreateInterfaceRequest) => void;
    onClose: () => void;
    onSubmit: () => void;
}) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 px-4">
            <div className="w-full max-w-2xl rounded-3xl bg-white p-7 shadow-2xl">
                <div className="flex items-start justify-between">
                    <div>
                        <h3 className="text-xl font-bold">{title}</h3>
                        <p className="mt-1 text-sm text-slate-500">
                            외부 기관과 연계할 인터페이스 정보를 입력합니다.
                        </p>
                    </div>

                    <button
                        onClick={onClose}
                        className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-semibold transition hover:bg-slate-50"
                    >
                        닫기
                    </button>
                </div>

                <div className="mt-6 grid gap-4 md:grid-cols-2">
                    <FormField label="외부 기관">
                        <select
                            value={form.organizationId}
                            onChange={(e) =>
                                onChange({ ...form, organizationId: Number(e.target.value) })
                            }
                            className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                        >
                            <option value={0}>기관 선택</option>
                            {organizations.map((organization) => (
                                <option key={organization.id} value={organization.id}>
                                    {organization.name}
                                </option>
                            ))}
                        </select>
                    </FormField>

                    <FormField label="프로토콜">
                        <select
                            value={form.protocolType}
                            onChange={(e) =>
                                onChange({ ...form, protocolType: e.target.value as Protocol })
                            }
                            className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                        >
                            <option value="REST">REST</option>
                            <option value="SOAP">SOAP</option>
                            <option value="SFTP">SFTP</option>
                            <option value="MQ">MQ</option>
                            <option value="BATCH">BATCH</option>
                        </select>
                    </FormField>

                    <FormField label="인터페이스명">
                        <input
                            value={form.name}
                            onChange={(e) => onChange({ ...form, name: e.target.value })}
                            className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                        />
                    </FormField>

                    <FormField label="담당팀">
                        <input
                            value={form.ownerTeam}
                            onChange={(e) => onChange({ ...form, ownerTeam: e.target.value })}
                            className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                        />
                    </FormField>

                    <div className="md:col-span-2">
                        <FormField label="엔드포인트">
                            <input
                                value={form.endpoint}
                                onChange={(e) => onChange({ ...form, endpoint: e.target.value })}
                                className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                            />
                        </FormField>
                    </div>

                    <div className="md:col-span-2">
                        <FormField label="설명">
                            <textarea
                                value={form.description}
                                onChange={(e) => onChange({ ...form, description: e.target.value })}
                                className="min-h-28 w-full rounded-xl border border-slate-200 px-3 py-3 text-sm"
                            />
                        </FormField>
                    </div>
                </div>

                <div className="mt-6 flex justify-end gap-2">
                    <button
                        onClick={onClose}
                        className="rounded-xl border border-slate-200 px-5 py-3 text-sm font-semibold transition hover:bg-slate-50"
                    >
                        취소
                    </button>
                    <button
                        onClick={onSubmit}
                        className="rounded-xl bg-blue-600 px-5 py-3 text-sm font-bold text-white transition hover:bg-blue-700"
                    >
                        저장
                    </button>
                </div>
            </div>
        </div>
    );
}

function FormField({
                       label,
                       children,
                   }: {
    label: string;
    children: React.ReactNode;
}) {
    return (
        <label className="block">
            <span className="mb-2 block text-sm font-bold text-slate-700">{label}</span>
            {children}
        </label>
    );
}

function InfoBox({ title, value }: { title: string; value: string }) {
    return (
        <div className="rounded-xl bg-slate-50 px-4 py-3 text-sm">
            <p className="font-bold text-slate-700">{title}</p>
            <p className="mt-1 text-slate-600">{value}</p>
        </div>
    );
}

function PayloadBox({ title, value }: { title: string; value: string }) {
    return (
        <div>
            <p className="mb-2 text-xs font-bold text-slate-500">{title}</p>
            <pre className="max-h-56 overflow-auto rounded-2xl bg-slate-950 p-4 text-xs leading-relaxed text-slate-100">
                {value}
            </pre>
        </div>
    );
}