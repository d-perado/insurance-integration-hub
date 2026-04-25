import type { Protocol } from "../../types/interface";

export default function ProtocolBadge({ protocol }: { protocol: Protocol }) {
    return (
        <span className="inline-flex rounded-full bg-slate-100 px-3 py-1 text-xs font-bold text-slate-700">
      {protocol}
    </span>
    );
}