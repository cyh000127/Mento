import { Link } from "react-router-dom"
import { Users, ArrowRight } from "lucide-react"

export function GuideCta() {
  return (
    <section className="bg-background py-16">
      <div className="mx-auto max-w-[1200px] px-6">
        <div className="flex flex-col items-center justify-between gap-8 rounded-2xl bg-gradient-to-r from-pastel-purple-100 to-pastel-green-100 p-8 md:flex-row md:p-12">
          {/* Content */}
          <div className="flex items-center gap-6">
            <div className="hidden h-16 w-16 items-center justify-center rounded-2xl bg-dark-bg md:flex">
              <Users className="h-8 w-8 text-primary-500" />
            </div>
            <div>
              <h3 className="mb-2 text-xl font-bold text-text-primary md:text-2xl">
                더 깊이 있는 조언이 필요하신가요?
              </h3>
              <p className="text-text-secondary">
                전문가 멘토와 1:1 상담을 통해 맞춤형 조언을 받아보세요.
                <br className="hidden md:block" />
                가이드만으로 해결되지 않는 고민을 해결해드립니다.
              </p>
            </div>
          </div>

          {/* CTA Button */}
          <Link
            to="/mentoring"
            className="inline-flex w-full items-center justify-center gap-2 rounded-xl bg-dark-bg px-8 py-3.5 font-medium text-primary-500 transition-all hover:bg-dark-bg/90 md:w-auto"
          >
            멘토 찾기
            <ArrowRight className="h-4 w-4" />
          </Link>
        </div>
      </div>
    </section>
  )
}
