import { initialInterfaces } from "../mock/interfaceData";
import StatusBadge from "../components/ui/StatusBadge";

export default function OrganizationPage() {
    const organizations = Array.from(
        new Set(initialInterfaces.map((item) => item.organization))
    );

    return (
        <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="text-lg font-bold">기관 관리</h3>
            <p className="mt-1 text-sm text-slate-500">
                외부 기관별 연계 인터페이스 현황과 장애 상태를 확인합니다.
            </p>

            <div className="mt-6 grid gap-4 md:grid-cols-2 xl:grid-cols-3">
                {organizations.map((organization) => {
                    const items = initialInterfaces.filter(
                        (item) => item.organization === organization
                    );
                    const failed = items.filter((item) => item.status === "FAILED").length;

                    return (
                        <div key={organization} className="rounded-2xl border border-slate-200 p-5">
                            <div className="flex items-center justify-between">
                                <h4 className="font-bold">{organization}</h4>
                                {failed > 0 ? (
                                    <span className="rounded-full bg-red-50 px-3 py-1 text-xs font-bold text-red-700">
                    장애 {failed}건
                  </span>
                                ) : (
                                    <span className="rounded-full bg-emerald-50 px-3 py-1 text-xs font-bold text-emerald-700">
                    정상
                  </span>
                                )}
                            </div>

                            <p className="mt-3 text-sm text-slate-500">
                                연계 인터페이스 {items.length}개
                            </p>

                            <div className="mt-4 space-y-2">
                                {items.map((item) => (
                                    <div
                                        key={item.id}
                                        className="flex items-center justify-between rounded-xl bg-slate-50 px-3 py-2 text-sm"
                                    >
                                        <span>{item.name}</span>
                                        <StatusBadge status={item.status} />
                                    </div>
                                ))}
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}