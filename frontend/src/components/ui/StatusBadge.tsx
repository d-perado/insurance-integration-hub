import type { Status } from "../../types/interface";

export default function StatusBadge({ status }: { status: Status }) {
    const config = {
        SUCCESS: "bg-emerald-50 text-emerald-700 ring-emerald-100",
        FAILED: "bg-red-50 text-red-700 ring-red-100",
        PENDING: "bg-amber-50 text-amber-700 ring-amber-100",
        RUNNING: "bg-blue-50 text-blue-700 ring-blue-100",
    };

    const label = {
        SUCCESS: "성공",
        FAILED: "실패",
        PENDING: "대기",
        RUNNING: "실행중",
    };

    const style =
        config[status] ??
        "bg-slate-50 text-slate-700 ring-slate-200";

    const text =
        label[status] ??
        "알 수 없음";

    return (
        <span
            className={`inline-flex rounded-full px-3 py-1 text-xs font-bold ring-1 ${style}`}
        >
            {text}
        </span>
    );
}