import { NavLink } from "react-router-dom";
import { useEffect, useState } from "react";
import { getOperationStatus } from "../../api/interfaceApi";

const menus = [
    { label: "인터페이스 모니터링", path: "/" },
    { label: "실행 이력", path: "/history" },
    { label: "기관 관리", path: "/organizations" },
    { label: "운영 설정", path: "/settings" },
];

type SidebarProps = {
    refreshKey: number;
};

export default function Sidebar({ refreshKey }: SidebarProps) {
    const [hasFailed, setHasFailed] = useState(false);

    useEffect(() => {
        const fetchOperationStatus = async () => {
            try {
                const data = await getOperationStatus();
                setHasFailed(data.hasFailed);
            } catch (error) {
                console.error(error);
            }
        };

        fetchOperationStatus();
    }, [refreshKey]);

    return (
        <aside className="fixed left-0 top-0 hidden h-screen w-64 border-r border-slate-200 bg-white px-5 py-6 lg:block">
            <div>
                <p className="text-xs font-semibold uppercase tracking-[0.2em] text-blue-600">
                    Insurance
                </p>
                <h1 className="mt-2 text-xl font-bold">Integration Hub</h1>
            </div>

            <nav className="mt-10 space-y-1 text-sm">
                {menus.map((menu) => (
                    <NavLink
                        key={menu.path}
                        to={menu.path}
                        className={({ isActive }: { isActive: boolean }) =>
                            `block rounded-xl px-4 py-3 font-medium transition ${
                                isActive
                                    ? "bg-blue-50 text-blue-700"
                                    : "text-slate-600 hover:bg-slate-50"
                            }`
                        }
                    >
                        {menu.label}
                    </NavLink>
                ))}
            </nav>

            <div
                className={`absolute bottom-6 left-5 right-5 rounded-2xl p-4 text-white ${
                    hasFailed ? "bg-slate-900" : "bg-emerald-600"
                }`}
            >
                <p className="text-sm font-semibold">운영 상태</p>
                <p className="mt-1 text-xs text-white/80">
                    {hasFailed
                        ? "실패 인터페이스 확인 필요"
                        : "전체 인터페이스 정상"}
                </p>
            </div>
        </aside>
    );
}