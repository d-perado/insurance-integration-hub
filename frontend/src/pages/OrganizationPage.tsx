import { useEffect, useState } from "react";
import type {
    CreateOrganizationRequest,
    Organization,
} from "../types/organization";
import {
    createOrganization,
    deleteOrganization,
    getOrganizations,
    updateOrganization,
} from "../api/organizationApi";

const emptyForm: CreateOrganizationRequest = {
    name: "",
    managerName: "",
    managerEmail: "",
};

export default function OrganizationPage() {
    const [organizations, setOrganizations] = useState<Organization[]>([]);
    const [isLoading, setIsLoading] = useState(false);
    const [errorMessage, setErrorMessage] = useState("");

    const [isFormOpen, setIsFormOpen] = useState(false);
    const [editingOrganization, setEditingOrganization] =
        useState<Organization | null>(null);
    const [form, setForm] =
        useState<CreateOrganizationRequest>(emptyForm);

    const loadOrganizations = async () => {
        setIsLoading(true);
        setErrorMessage("");

        try {
            const data = await getOrganizations();
            setOrganizations(data);
        } catch (error) {
            setErrorMessage(
                error instanceof Error
                    ? error.message
                    : "기관 목록 조회 중 오류가 발생했습니다."
            );
        } finally {
            setIsLoading(false);
        }
    };

    useEffect(() => {
        loadOrganizations();
    }, []);

    const openCreateForm = () => {
        setEditingOrganization(null);
        setForm(emptyForm);
        setIsFormOpen(true);
    };

    const openEditForm = (organization: Organization) => {
        setEditingOrganization(organization);

        setForm({
            name: organization.name,
            managerName: organization.managerName,
            managerEmail: organization.managerEmail,
        });

        setIsFormOpen(true);
    };

    const submitForm = async () => {
        try {
            if (editingOrganization) {
                await updateOrganization(editingOrganization.id, form);
            } else {
                await createOrganization(form);
            }

            setIsFormOpen(false);
            setEditingOrganization(null);
            setForm(emptyForm);

            await loadOrganizations();
        } catch (error) {
            setErrorMessage(
                error instanceof Error
                    ? error.message
                    : "기관 저장 중 오류가 발생했습니다."
            );
        }
    };

    const handleDelete = async (organizationId: number) => {
        const confirmed = window.confirm(
            "외부 기관을 삭제하시겠습니까?"
        );

        if (!confirmed) return;

        try {
            await deleteOrganization(organizationId);
            await loadOrganizations();
        } catch (error) {
            setErrorMessage(
                error instanceof Error
                    ? error.message
                    : "기관 삭제 중 오류가 발생했습니다."
            );
        }
    };

    return (
        <>
            <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
                <div className="flex flex-col gap-4 2xl:flex-row 2xl:items-start 2xl:justify-between">
                    <div>
                        <h3 className="text-lg font-bold">
                            기관 관리
                        </h3>

                        <p className="mt-1 text-sm text-slate-500">
                            외부 기관의 담당자 정보를 등록하고 관리합니다.
                        </p>
                    </div>

                    <button
                        onClick={openCreateForm}
                        className="rounded-xl bg-blue-600 px-5 py-3 text-sm font-bold text-white transition hover:bg-blue-700"
                    >
                        기관 등록
                    </button>
                </div>

                {errorMessage && (
                    <div className="mt-5 rounded-2xl border border-red-100 bg-red-50 px-5 py-4 text-sm font-medium text-red-700">
                        {errorMessage}
                    </div>
                )}

                <div className="mt-6 overflow-x-auto rounded-2xl border border-slate-200">
                    <table className="min-w-[900px] w-full text-sm">
                        <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                        <tr>
                            <th className="px-4 py-3">
                                기관명
                            </th>
                            <th className="px-4 py-3">
                                담당자
                            </th>
                            <th className="px-4 py-3">
                                이메일
                            </th>
                            <th className="px-4 py-3">
                                등록일시
                            </th>
                            <th className="px-4 py-3 text-right">
                                작업
                            </th>
                        </tr>
                        </thead>

                        <tbody className="divide-y divide-slate-100">
                        {isLoading ? (
                            <tr>
                                <td
                                    colSpan={5}
                                    className="px-4 py-10 text-center text-slate-500"
                                >
                                    데이터를 불러오는 중입니다.
                                </td>
                            </tr>
                        ) : organizations.length === 0 ? (
                            <tr>
                                <td
                                    colSpan={5}
                                    className="px-4 py-10 text-center text-slate-500"
                                >
                                    등록된 외부 기관이 없습니다.
                                </td>
                            </tr>
                        ) : (
                            organizations.map((organization) => (
                                <tr
                                    key={organization.id}
                                    className="hover:bg-slate-50"
                                >
                                    <td className="px-4 py-4 font-semibold">
                                        {organization.name}
                                    </td>

                                    <td className="px-4 py-4">
                                        {organization.managerName}
                                    </td>

                                    <td className="px-4 py-4 text-slate-600">
                                        {organization.managerEmail}
                                    </td>

                                    <td className="px-4 py-4 text-slate-500">
                                        {organization.createdAt}
                                    </td>

                                    <td className="px-4 py-4">
                                        <div className="flex justify-end gap-2">
                                            <button
                                                onClick={() =>
                                                    openEditForm(
                                                        organization
                                                    )
                                                }
                                                className="rounded-xl border border-slate-200 px-3 py-2 text-xs font-semibold transition hover:bg-slate-100"
                                            >
                                                수정
                                            </button>

                                            <button
                                                onClick={() =>
                                                    handleDelete(
                                                        organization.id
                                                    )
                                                }
                                                className="rounded-xl bg-red-600 px-3 py-2 text-xs font-semibold text-white transition hover:bg-red-700"
                                            >
                                                삭제
                                            </button>
                                        </div>
                                    </td>
                                </tr>
                            ))
                        )}
                        </tbody>
                    </table>
                </div>
            </div>

            {isFormOpen && (
                <OrganizationFormModal
                    title={
                        editingOrganization
                            ? "기관 수정"
                            : "기관 등록"
                    }
                    form={form}
                    onChange={setForm}
                    onClose={() => setIsFormOpen(false)}
                    onSubmit={submitForm}
                />
            )}
        </>
    );
}

function OrganizationFormModal({
                                   title,
                                   form,
                                   onChange,
                                   onClose,
                                   onSubmit,
                               }: {
    title: string;
    form: CreateOrganizationRequest;
    onChange: (form: CreateOrganizationRequest) => void;
    onClose: () => void;
    onSubmit: () => void;
}) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-slate-950/50 px-4">
            <div className="w-full max-w-xl rounded-3xl bg-white p-7 shadow-2xl">
                <div className="flex items-start justify-between">
                    <div>
                        <h3 className="text-xl font-bold">
                            {title}
                        </h3>

                        <p className="mt-1 text-sm text-slate-500">
                            외부 기관과 담당자 정보를 입력합니다.
                        </p>
                    </div>

                    <button
                        onClick={onClose}
                        className="rounded-xl border border-slate-200 px-4 py-2 text-sm font-semibold transition hover:bg-slate-50"
                    >
                        닫기
                    </button>
                </div>

                <div className="mt-6 space-y-4">
                    <FormField label="기관명">
                        <input
                            value={form.name}
                            onChange={(e) =>
                                onChange({
                                    ...form,
                                    name: e.target.value,
                                })
                            }
                            className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                        />
                    </FormField>

                    <FormField label="담당자명">
                        <input
                            value={form.managerName}
                            onChange={(e) =>
                                onChange({
                                    ...form,
                                    managerName:
                                    e.target.value,
                                })
                            }
                            className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                        />
                    </FormField>

                    <FormField label="담당자 이메일">
                        <input
                            value={form.managerEmail}
                            onChange={(e) =>
                                onChange({
                                    ...form,
                                    managerEmail:
                                    e.target.value,
                                })
                            }
                            className="h-11 w-full rounded-xl border border-slate-200 px-3 text-sm"
                        />
                    </FormField>
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
            <span className="mb-2 block text-sm font-bold text-slate-700">
                {label}
            </span>

            {children}
        </label>
    );
}