import { Outlet } from "react-router-dom";
import Sidebar from "./Sidebar";

export default function AppLayout() {
    return (
        <div className="min-h-screen bg-[#F6F8FB] text-slate-900">
            <Sidebar />

            <main className="lg:pl-64">
                <header className="sticky top-0 z-20 border-b border-slate-200 bg-white/80 backdrop-blur">
                    <div className="flex items-center justify-between px-6 py-5">
                        <div>
                            <p className="text-sm text-slate-500">
                                금융 IT 인터페이스 통합관리시스템
                            </p>
                            <h2 className="mt-1 text-2xl font-bold">
                                Insurance Integration Hub
                            </h2>
                        </div>

                        <div className="hidden items-center gap-3 md:flex">
              <span className="rounded-full bg-blue-50 px-3 py-1 text-sm font-medium text-blue-700">
                운영 담당자
              </span>
                            <span className="text-sm text-slate-500">
                기준 시각 2026-04-25 22:45
              </span>
                        </div>
                    </div>
                </header>

                <section className="px-6 py-6">
                    <Outlet />
                </section>
            </main>
        </div>
    );
}