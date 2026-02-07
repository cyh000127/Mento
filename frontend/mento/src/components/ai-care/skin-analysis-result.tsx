import { Droplets, Search, Minus, Sun, Dumbbell, Sparkles } from "lucide-react";
import type { SkinAnalysisDetailData } from "@/types/ai-skincare";

const METRIC_STYLE_MAP = {
  moisture: {
    label: "수분",
    icon: Droplets,
    color: "text-primary-500",
    bgColor: "bg-[#beeffc]",
    cardGradient: "from-[#beeffc] to-[#ccf8ff]",
    cardBg: "bg-[#ccf8ff]/50",
    textColor: "text-dark-bg",
  },
  pore: {
    label: "모공",
    icon: Search,
    color: "text-[#6fb896]",
    bgColor: "bg-[#bfeedd]",
    cardGradient: "from-[#bfeedd] to-[#dffaf0]",
    cardBg: "bg-[#dffaf0]/50",
    textColor: "text-dark-bg",
  },
  wrinkle: {
    label: "주름",
    icon: Minus,
    color: "text-[#9b93d4]",
    bgColor: "bg-[#e6e3fa]",
    cardGradient: "from-[#e6e3fa] to-[#f2f0ff]",
    cardBg: "bg-[#f2f0ff]/50",
    textColor: "text-dark-bg",
  },
  pigmentation: {
    label: "색소침착",
    icon: Sun,
    color: "text-primary-500",
    bgColor: "bg-[#ccf8ff]",
    cardGradient: "from-[#ccf8ff] to-[#beeffc]",
    cardBg: "bg-[#beeffc]/50",
    textColor: "text-dark-bg",
  },
  sagging: {
    label: "탄력",
    icon: Dumbbell,
    color: "text-[#6fb896]",
    bgColor: "bg-[#dffaf0]",
    cardGradient: "from-[#dffaf0] to-[#bfeedd]",
    cardBg: "bg-[#bfeedd]/50",
    textColor: "text-dark-bg",
  },
};

interface SkinAnalysisResultProps {
  analysisResult: SkinAnalysisDetailData;
  onRetry?: () => void;
  showRetryButton?: boolean;
}

export function SkinAnalysisResult({ analysisResult, onRetry, showRetryButton = false }: SkinAnalysisResultProps) {
  const getStatusTextClass = (status: string) => {
    if (status === "우수") return "text-green-700";
    if (status === "양호") return "text-primary-600";
    if (status === "보통") return "text-orange-600";
    if (status === "주의") return "text-red-600";
    return "text-text-secondary";
  };

  const getStatusFromGrade = (grade: number) => {
    if (grade === 1) return "우수";
    if (grade === 2) return "양호";
    if (grade === 3) return "보통";
    return "주의";
  };

  const getUiLevelFromGrade = (grade: number) => 6 - grade;

  const totalScore = 100 - analysisResult?.total_score;
  const totalGrade = analysisResult?.total_grade;
  const totalStatus = totalGrade ? getStatusFromGrade(totalGrade) : "";
  const totalLevel = totalGrade ? getUiLevelFromGrade(totalGrade) : 0;
  const skinType = analysisResult?.skin_type_summary ?? "";
  const details = analysisResult?.details;
  const metricOrder: Array<keyof typeof METRIC_STYLE_MAP> = ["moisture", "pore", "wrinkle", "pigmentation", "sagging"];
  const orderedMetrics = metricOrder
    .map((key) => {
      const detail = details?.[key];
      if (!detail) return null;
      return { key, detail, style: METRIC_STYLE_MAP[key] };
    })
    .filter((item): item is { key: keyof typeof METRIC_STYLE_MAP; detail: any; style: (typeof METRIC_STYLE_MAP)[keyof typeof METRIC_STYLE_MAP] } => Boolean(item));
  const summaryItems = orderedMetrics.slice(0, 2).map((item) => ({
    label: item.style.label,
    status: getStatusFromGrade(item.detail.grade),
  }));

  return (
    <div className="mx-auto max-w-6xl">
      {/* Header */}
      <div className="mb-8 text-center">
        <h2 className="mb-2 text-4xl font-bold text-text-primary md:text-4xl">피부 분석 결과</h2>
        <p className="text-text-secondary">AI가 분석한 당신의 피부 상태를 확인해보세요</p>
      </div>

      {/* Top Summary Section */}
      <div className="mb-8 grid gap-6 md:grid-cols-2">
        {/* Total Score Card */}
        <div className="relative rounded-3xl bg-gradient-to-br from-primary-400 via-primary-450 to-primary-500 p-10 text-center shadow-xl">
          {/* 타이틀 */}
          <p className="mb-4 text-md font-semibold tracking-wide text-white/80">피부 종합 점수</p>

          {/* 점수 영역 */}
          <div className="mb-6 flex items-end justify-center gap-4">
            <p className="text-8xl font-extrabold leading-none text-white drop-shadow-sm">{totalScore ?? ""}</p>

            <div className="flex flex-col items-start pb-2">
              <p className="text-2xl font-semibold text-white/90">점</p>
              <div className="mt-2 flex gap-1.5">
                {[1, 2, 3, 4, 5].map((star) => (
                  <span key={star} className={`h-3.5 w-3.5 rounded-full transition-all ${totalLevel && star <= totalLevel ? "bg-white" : "bg-white/30"}`} />
                ))}
              </div>
            </div>
          </div>

          {/* 상태 (양호) */}
          {totalStatus && (
            <div className="mx-auto inline-flex items-center justify-center rounded-full bg-white/20 px-8 py-3 backdrop-blur-sm">
              <p className="text-2xl font-bold tracking-wide text-white">{totalStatus}</p>
            </div>
          )}
        </div>

        {/* Skin Type Summary Card */}
        <div className="rounded-3xl border-2 border-primary-300 bg-gradient-to-br from-primary-50 to-white p-8 shadow-lg">
          <p className="mb-3 text-md font-medium text-text-secondary">피부 타입</p>
          <p className="mb-4 text-4xl font-bold text-primary-600">{skinType ?? ""}</p>
          <div className="space-y-2">
            {summaryItems.map((item, idx) => (
              <div key={`${item.label}-${idx}`} className="flex items-center justify-between rounded-lg bg-white/60 px-3 py-2">
                <span className="text-md font-medium text-text-primary">{item.label}</span>
                <span className={`text-md font-semibold ${getStatusTextClass(item.status)}`}>{item.status}</span>
              </div>
            ))}
          </div>
        </div>
      </div>

      {/* Central Visualization Section */}
      <div className="mb-8 rounded-3xl border border-border bg-gradient-to-br from-primary-50/50 to-white p-8 shadow-lg">
        <h3 className="mb-6 text-center text-2xl font-bold text-text-primary">피부 상태 상세 분석</h3>

        <div className="mx-auto flex max-w-5xl flex-col items-center gap-6 lg:flex-row lg:items-center">
          {/* Radar Chart */}
          <div className="relative flex w-full max-w-lg items-center justify-center lg:w-1/2">
            <svg viewBox="0 0 500 500" className="h-full w-full" style={{ maxHeight: "500px" }}>
              {/* Background circles */}
              {[180, 144, 108, 72, 36].map((radius, idx) => (
                <circle key={`circle-${idx}`} cx="250" cy="250" r={radius} fill="none" stroke={idx === 0 ? "#b8f6ff" : "#e5e7eb"} strokeWidth="2" />
              ))}

              {/* Guide lines */}
              {[0, 72, 144, 216, 288].map((angle, idx) => {
                const radian = ((angle - 90) * Math.PI) / 180;
                const x = 250 + 180 * Math.cos(radian);
                const y = 250 + 180 * Math.sin(radian);
                return <line key={`line-${idx}`} x1="250" y1="250" x2={x} y2={y} stroke="#d1d5db" strokeWidth="2" />;
              })}

              {/* Data polygon */}
              <polygon
                points={orderedMetrics
                  .map(({ detail }, idx) => {
                    const angle = [0, 72, 144, 216, 288][idx];
                    const radian = ((angle - 90) * Math.PI) / 180;
                    // 점수가 낮을수록 바깥쪽 (100 - value로 반전, 그리고 1.8배 스케일)
                    const radius = (100 - detail.score) * 1.8;
                    const x = 250 + radius * Math.cos(radian);
                    const y = 250 + radius * Math.sin(radian);
                    return `${x},${y}`;
                  })
                  .join(" ")}
                fill="rgba(134, 239, 172, 0.3)"
                stroke="#86efac"
                strokeWidth="4"
              />

              {/* Data points - circles removed, icons will be overlaid */}
              {orderedMetrics.map(({ style }, idx) => {
                const angle = [0, 72, 144, 216, 288][idx];
                const radian = ((angle - 90) * Math.PI) / 180;
                const labelX = 250 + 210 * Math.cos(radian);
                const labelY = 250 + 210 * Math.sin(radian);

                return (
                  <g key={`point-${idx}`}>
                    <text x={labelX} y={labelY} textAnchor="middle" dominantBaseline="middle" className="text-xl font-bold" fill="#111827">
                      {style.label}
                    </text>
                  </g>
                );
              })}
            </svg>

            {/* Data point icons overlay */}
            <div className="absolute inset-0" style={{ pointerEvents: "none" }}>
              {orderedMetrics.map(({ detail, style }, idx) => {
                const angle = [0, 72, 144, 216, 288][idx];
                const radian = ((angle - 90) * Math.PI) / 180;
                const radius = (100 - detail.score) * 1.8;
                // SVG viewBox는 500x500이고, 중심은 250,250
                // 실제 위치 계산: 중심(250) + radius * cos/sin
                const svgX = 250 + radius * Math.cos(radian);
                const svgY = 250 + radius * Math.sin(radian);
                // SVG 좌표를 퍼센트로 변환 (viewBox 500 기준)
                const percentX = (svgX / 500) * 100;
                const percentY = (svgY / 500) * 100;

                return (
                  <div
                    key={idx}
                    className={`absolute flex h-7 w-7 items-center justify-center rounded-full ${style.bgColor} border-2 border-white shadow-md`}
                    style={{
                      left: `${percentX}%`,
                      top: `${percentY}%`,
                      transform: `translate(-50%, -50%)`,
                    }}
                  >
                    <style.icon className={`h-4 w-4 ${style.color}`} />
                  </div>
                );
              })}
            </div>
          </div>

          {/* Legend / Quick Stats */}
          <div className="w-full space-y-3 lg:w-1/2">
            {orderedMetrics.map(({ detail, style }, idx) => {
              const status = getStatusFromGrade(detail.grade);
              return (
                <div key={idx} className="flex items-center justify-between rounded-xl border border-border bg-white p-4 shadow-sm transition-all hover:shadow-md">
                  <div className="flex items-center gap-3">
                    <div className={`flex h-8 w-8 items-center justify-center rounded-full ${style.bgColor}`}>
                      <style.icon className={`h-4 w-4 ${style.color}`} />
                    </div>
                    <span className="font-medium text-text-primary">{style.label}</span>
                  </div>
                  <div className="flex items-center gap-3">
                    <span className="text-md font-semibold text-text-secondary">{100 - detail.score}점</span>
                    <span
                      className={`rounded-full px-3 py-1 text-xs font-medium ${
                        status === "우수"
                          ? "bg-green-100 text-green-700 font-semibold"
                          : status === "양호"
                          ? "bg-primary-100 text-primary-500 font-semibold"
                          : status === "보통"
                          ? "bg-orange-100 text-orange-600 font-semibold"
                          : status === "주의"
                          ? "bg-red-100 text-red-600 font-semibold"
                          : "bg-muted text-text-secondary"
                      }`}
                    >
                      {status}
                    </span>
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>

      {/* Detail Cards Section */}
      <div className="mb-8 grid gap-4 md:grid-cols-2 lg:grid-cols-3">
        {orderedMetrics.map(({ detail, style }, idx) => {
          return (
            <div key={idx} className={`overflow-hidden rounded-2xl border border-border ${style.cardBg} shadow-md transition-all hover:shadow-lg`}>
              <div className={`bg-gradient-to-r ${style.cardGradient} px-6 py-4 ${style.textColor}`}>
                <div className="flex items-center justify-between">
                  <div className="flex items-center gap-2">
                    <style.icon className={`h-6 w-6 ${style.color}`} />
                    <h4 className="text-xl font-bold">{style.label}</h4>
                  </div>
                  <div className="text-right">
                    <p className="text-3xl font-bold">{100 - detail.score}</p>
                    <p className="text-xs opacity-90">점</p>
                  </div>
                </div>
              </div>
              <div className="p-6">
                <div className="mb-3 flex items-center gap-1">
                  {[...Array(5)].map((_, i) => (
                    <div key={i} className={`h-2 w-2 rounded-full ${i < getUiLevelFromGrade(detail.grade) ? "bg-[#22c55e]" : "bg-gray-300"}`} />
                  ))}
                  <span className="ml-2 text-sm text-text-secondary">{getUiLevelFromGrade(detail.grade)}/5단계</span>
                </div>

                <p className="text-sm leading-relaxed text-text-secondary">{detail.description}</p>
              </div>
            </div>
          );
        })}
      </div>

      {/* Retry Button */}
      {showRetryButton && onRetry && (
        <div className="mt-8 text-center">
          <button
            type="button"
            onClick={onRetry}
            className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-8 py-4 font-medium text-dark-bg shadow-lg transition-all hover:bg-primary-400 hover:shadow-xl hover:scale-105"
          >
            <Sparkles className="h-5 w-5" />
            다시 분석하기
          </button>
        </div>
      )}
    </div>
  );
}
