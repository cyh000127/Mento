import { Users, MessageCircle, Award } from "lucide-react"

export function MentoringHero() {
  return (
    <section className="relative overflow-hidden bg-gradient-to-br from-pastel-purple-100/50 via-background to-primary-100/30">
      <div className="mx-auto max-w-[1200px] px-6 py-16 md:py-24">
        <div className="flex flex-col items-center text-center">
          {/* Badge */}
          <div className="mb-6 inline-flex items-center gap-2 rounded-full bg-pastel-purple-200 px-4 py-1.5">
            <Users className="h-4 w-4 text-text-primary" />
            <span className="text-sm font-medium text-text-primary">
              Expert Guidance
            </span>
          </div>

          {/* Title */}
          <h1 className="mb-4 text-balance text-3xl font-bold text-text-primary md:text-4xl lg:text-5xl">
            전문가 멘토링
          </h1>

          {/* Description */}
          <p className="mb-12 max-w-xl text-pretty text-base leading-relaxed text-text-secondary md:text-lg">
            검증된 그루밍 전문가와 1:1 상담을 통해 나만의 루틴을 설계하세요.
            피부 관리부터 헤어 스타일링까지 전문적인 조언을 받을 수 있습니다.
          </p>

          {/* Stats */}
          <div className="grid w-full max-w-2xl grid-cols-3 gap-6">
            <div className="rounded-2xl bg-background p-6 shadow-sm">
              <div className="mb-2 flex items-center justify-center">
                <Award className="h-6 w-6 text-primary-500" />
              </div>
              <p className="text-2xl font-bold text-text-primary">50+</p>
              <p className="text-sm text-text-secondary">전문 멘토</p>
            </div>
            <div className="rounded-2xl bg-background p-6 shadow-sm">
              <div className="mb-2 flex items-center justify-center">
                <MessageCircle className="h-6 w-6 text-primary-500" />
              </div>
              <p className="text-2xl font-bold text-text-primary">12,000+</p>
              <p className="text-sm text-text-secondary">상담 완료</p>
            </div>
            <div className="rounded-2xl bg-background p-6 shadow-sm">
              <div className="mb-2 flex items-center justify-center">
                <Users className="h-6 w-6 text-primary-500" />
              </div>
              <p className="text-2xl font-bold text-text-primary">4.9</p>
              <p className="text-sm text-text-secondary">평균 만족도</p>
            </div>
          </div>
        </div>
      </div>
    </section>
  )
}
