import { Users, MessageCircle, Award, ArrowRight } from "lucide-react";
import { Link } from "react-router-dom";
export function MentoringHero() {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-pastel-purple-100/50 via-background to-primary-100/30">
      <div className="mx-auto max-w-[1200px] px-6 py-16 md:py-24">
        <div className="flex flex-col items-center text-center">
          {/* Badge */}
          <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-pastel-purple-200 px-4 py-1.5">
            <Users className="h-4 w-4 text-text-primary" />
            <span className="text-sm font-medium text-text-primary">Expert Guidance</span>
          </div>

          {/* Title */}
          <h1 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl lg:text-5xl">전문가 멘토링</h1>

          {/* Description */}
          <p className="max-w-xl text-pretty text-base leading-relaxed text-text-secondary md:text-lg">
            전문가와 함께 만드는 나만의 관리 루틴 피부부터 헤어까지,
            <br /> 1:1 맞춤 상담으로 해결하세요.
          </p>

          {/* CTA Button */}
          <div className="mt-12 flex justify-center">
            <Link
              to="/consultation"
              onClick={() => window.scrollTo({ top: 0, behavior: "smooth" })}
              className="group relative inline-flex items-center gap-3 rounded-full bg-primary-500 px-12 py-5 text-xl font-bold text-white shadow-2xl shadow-primary-500/50 transition-all duration-300 hover:scale-110 hover:bg-primary-400 hover:shadow-primary-500/60"
            >
              <span>멘토링 예약하기</span>
              <ArrowRight className="h-6 w-6 transition-transform duration-300 group-hover:translate-x-2" />
            </Link>
          </div>
        </div>
      </div>
    </section>
  );
}
