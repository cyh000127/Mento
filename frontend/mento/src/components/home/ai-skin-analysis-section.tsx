import { useState } from "react";
import { ArrowRight } from "lucide-react";
import { Link } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { LoginModal } from "@/components/login-modal";
import { useAuthStore } from "@/stores/useAuthStore";
import aiSkinAnalysisImage from "@/assets/images/home/ai-skin-analysis.png";

export function AiSkinAnalysisSection() {
  const isLoggedIn = useAuthStore((state) => state.isLoggedIn);
  const [isLoginOpen, setIsLoginOpen] = useState(false);

  return (
    <>
      <section id="ai-skin-analysis-section" data-home-section className="relative h-screen snap-start snap-always bg-background overflow-hidden">
        <div className="relative z-10 mx-auto flex h-full max-w-[1200px] flex-col justify-center px-6 py-20">
          <div className="grid items-center gap-10 lg:grid-cols-2">
            <div className="space-y-6">
              <h2 className="text-balance text-4xl font-bold text-text-primary md:text-5xl lg:text-6xl">AI를 이용한 피부 분석</h2>
              <div className="text-pretty text-lg leading-relaxed text-text-secondary md:text-xl">
                <p>
                  사진을 업로드하면 수분, 모공, 주름, 색소 침착, 탄력 등
                  <br className="hidden md:block" />
                  피부 상태에 대한 AI 기반 분석 정보를 얻을 수 있습니다.
                </p>
                <br className="hidden md:block" />
                <p>
                  빠르고 간편하며 개인 맞춤형 분석을 통해 일상 루틴을
                  <br className="hidden md:block" />
                  순조롭게 유지할 수 있습니다.
                </p>
              </div>
              <div className="flex flex-col items-start gap-3 sm:flex-row">
                {isLoggedIn ? (
                  <Button asChild size="lg">
                    <Link to="/ai-care">
                      AI 피부 분석 가기
                      <ArrowRight className="h-5 w-5" />
                    </Link>
                  </Button>
                ) : (
                  <Button size="lg" onClick={() => setIsLoginOpen(true)}>
                    로그인하고 AI 피부 분석 가기
                    <ArrowRight className="h-5 w-5" />
                  </Button>
                )}
              </div>
            </div>

            <div className="overflow-hidden rounded-2xl border border-border/50 bg-background shadow-sm">
              <div className="aspect-[4/3] w-full">
                <img src={aiSkinAnalysisImage} alt="AI skin analysis result" className="h-full w-full object-contain" loading="lazy" />
              </div>
            </div>
          </div>
        </div>
      </section>
      <LoginModal isOpen={isLoginOpen} onClose={() => setIsLoginOpen(false)} />
    </>
  );
}
