export default function SettingsPage() {
    return (
        <div className="rounded-3xl border border-slate-200 bg-white p-6 shadow-sm">
            <h3 className="text-lg font-bold">운영 정책</h3>

            <p className="mt-1 text-sm text-slate-500">
                현재 시스템에서 적용 중인 장애 판단 기준과 운영 정책을 확인합니다.
            </p>

            <div className="mt-6 grid gap-5 2xl:grid-cols-2">
                <div className="rounded-2xl border border-slate-200 p-5">
                    <h4 className="font-bold">장애 판단 기준</h4>

                    <div className="mt-4 space-y-4">
                        <SettingRow
                            label="응답 지연 기준"
                            value="1,000ms 초과"
                        />

                        <SettingRow
                            label="재시도 횟수"
                            value="최대 3회"
                        />

                        <SettingRow
                            label="배치 대기 허용 시간"
                            value="30분"
                        />
                    </div>
                </div>

                <div className="rounded-2xl border border-slate-200 p-5">
                    <h4 className="font-bold">운영 알림 정책</h4>

                    <div className="mt-4 space-y-4">
                        <SettingRow
                            label="실패 발생 시"
                            value="운영 담당자 확인 대상"
                        />

                        <SettingRow
                            label="연속 실패 시"
                            value="담당 개발팀 확인 대상"
                        />

                        <SettingRow
                            label="일일 리포트"
                            value="매일 09:00 기준 점검"
                        />
                    </div>
                </div>
            </div>
        </div>
    );
}

function SettingRow({
                        label,
                        value,
                    }: {
    label: string;
    value: string;
}) {
    return (
        <div className="flex flex-col gap-1 rounded-xl bg-slate-50 px-4 py-3 text-sm 2xl:flex-row 2xl:items-center 2xl:justify-between">
            <span className="font-medium text-slate-600">
                {label}
            </span>

            <span className="font-bold text-slate-900">
                {value}
            </span>
        </div>
    );
}