import type { Status } from "../../types/interface";

export default function StatusBadge({ status }: { status: Status }) {
    const config = {
        SUCCESS: "bg-emerald-50 text-emerald-700 ring-emerald-100",
        FAILED: "bg-red-50 text-red-700 ring-red-100",
        PENDING: "bg-amber-50 text-amber-700 ring-amber-100",
    };

    const label = {
        SUCCESS: "성공",
        FAILED: "실패",
        PENDING: "대기",
    };

    return (
        <span className={`inline-flex rounded-full px-3 py-1 text-xs font-bold ring-1 ${config[status]}`}>
      {label[status]}
    </span>
    );
}