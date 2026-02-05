import { Cpu, Sparkles, Zap } from "lucide-react";

export function AiCareHero() {
  return (
    <section className="relative overflow-hidden bg-dark-bg">
      {/* Background effects */}
      <div className="absolute inset-0">
        <div className="absolute left-1/4 top-1/4 h-96 w-96 rounded-full bg-primary-500/10 blur-3xl" />
        <div className="absolute bottom-1/4 right-1/4 h-80 w-80 rounded-full bg-primary-400/10 blur-3xl" />
      </div>

      <div className="relative mx-auto max-w-[1200px] px-6 py-20 md:py-32">
        <div className="flex flex-col items-center text-center">
          {/* Badge */}
          <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-primary-500/20 px-4 py-1.5">
            <Cpu className="h-4 w-4 text-primary-400" />
            <span className="text-sm font-medium text-primary-400">AI Technology</span>
          </div>

          {/* Title */}
          <h1 className="mb-6 text-balance text-3xl font-bold text-white md:text-4xl lg:text-5xl">
            AI가 분석하는
            <br />
            <span className="text-primary-400">맞춤 스킨케어</span>
          </h1>

          {/* Description */}
          <p className="mb-12 max-w-xl text-pretty text-base leading-relaxed text-white/70 md:text-lg">최신 AI 기술로 피부 상태를 정밀 분석하고 데이터 기반의 맞춤형 케어 솔루션을 제안합니다.</p>

          {/* CTA */}
          <button
            type="button"
            onClick={() => {
              const analysisSection = document.getElementById("skin-analysis");
              if (analysisSection) {
                analysisSection.scrollIntoView({ behavior: "smooth", block: "start" });
              }
            }}
            className="inline-flex items-center gap-2 rounded-xl bg-primary-500 px-8 py-4 font-medium text-dark-bg shadow-lg shadow-primary-500/25 transition-all hover:bg-primary-400 hover:shadow-xl hover:shadow-primary-500/30"
          >
            <Sparkles className="h-5 w-5" />
            무료 피부 분석 시작
          </button>
        </div>
      </div>
    </section>
  );
}
