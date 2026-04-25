import { initialInterfaces, initialLogs } from "../mock/interfaceData";
import StatusBadge from "../components/ui/StatusBadge";

export default function ExecutionHistoryPage() {
    const getInterface = (id: number) =>
        initialInterfaces.find((item) => item.id === id);

    return (
        <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="text-lg font-bold">실행 이력</h3>
            <p className="mt-1 text-sm text-slate-500">
                인터페이스 실행 결과와 실패 원인을 시간순으로 확인합니다.
            </p>

            <div className="mt-5 overflow-hidden rounded-2xl border border-slate-200">
                <table className="w-full text-sm">
                    <thead className="bg-slate-50 text-left text-xs font-semibold uppercase tracking-wide text-slate-500">
                    <tr>
                        <th className="px-4 py-3">실행 시간</th>
                        <th className="px-4 py-3">인터페이스</th>
                        <th className="px-4 py-3">기관</th>
                        <th className="px-4 py-3">상태</th>
                        <th className="px-4 py-3">응답시간</th>
                        <th className="px-4 py-3">메시지</th>
                    </tr>
                    </thead>

                    <tbody className="divide-y divide-slate-100">
                    {initialLogs.map((log) => {
                        const target = getInterface(log.interfaceId);

                        return (
                            <tr key={log.id} className="hover:bg-slate-50">
                                <td className="px-4 py-4 text-slate-600">{log.executedAt}</td>
                                <td className="px-4 py-4 font-semibold">{target?.name}</td>
                                <td className="px-4 py-4">{target?.organization}</td>
                                <td className="px-4 py-4">
                                    <StatusBadge status={log.status} />
                                </td>
                                <td className="px-4 py-4">{log.responseTimeMs}ms</td>
                                <td className="px-4 py-4 text-slate-600">
                                    {log.errorMessage ?? "정상 처리"}
                                </td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}