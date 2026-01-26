import { useState } from "react"
import { ChevronDown } from "lucide-react"

const faqs = [
  {
    question: "멘토링은 어떤 방식으로 진행되나요?",
    answer:
      "멘토링은 화상 통화 또는 채팅 상담으로 진행됩니다. 예약 시 원하는 방식을 선택하실 수 있으며, 대부분의 멘토가 두 가지 방식 모두 지원합니다.",
  },
  {
    question: "상담 시간은 얼마나 되나요?",
    answer:
      "기본 상담은 30분 단위로 진행됩니다. 심층 상담이 필요한 경우 60분 또는 90분 세션을 예약하실 수 있습니다. 멘토마다 제공하는 세션 옵션이 다를 수 있습니다.",
  },
  {
    question: "예약 취소는 어떻게 하나요?",
    answer:
      "예약은 상담 24시간 전까지 무료로 취소 가능합니다. 24시간 이내 취소 시 취소 수수료가 발생할 수 있습니다. 마이페이지에서 예약 내역을 확인하고 취소할 수 있습니다.",
  },
  {
    question: "상담 후 추가 질문이 있으면 어떻게 하나요?",
    answer:
      "상담 후 7일 이내에 간단한 추가 질문은 채팅으로 무료로 문의하실 수 있습니다. 심층적인 추가 상담이 필요한 경우 별도 세션을 예약해 주세요.",
  },
  {
    question: "멘토의 자격은 어떻게 검증되나요?",
    answer:
      "모든 멘토는 해당 분야 최소 5년 이상의 경력과 관련 자격증을 보유하고 있습니다. MENTO 팀이 직접 인터뷰와 배경 조사를 진행하여 검증된 전문가만 멘토로 활동합니다.",
  },
]

export function MentoringFaq() {
  const [openIndex, setOpenIndex] = useState<number | null>(0)

  return (
    <section className="bg-background py-16 md:py-24">
      <div className="mx-auto max-w-[800px] px-6">
        {/* Section Header */}
        <div className="mb-12 text-center">
          <p className="mb-3 text-sm font-medium uppercase tracking-wider text-primary-500">
            FAQ
          </p>
          <h2 className="mb-4 text-balance text-2xl font-bold text-text-primary md:text-3xl">
            자주 묻는 질문
          </h2>
        </div>

        {/* FAQ List */}
        <div className="space-y-4">
          {faqs.map((faq, index) => (
            <div
              key={index}
              className="overflow-hidden rounded-xl border border-border bg-background"
            >
              <button
                type="button"
                onClick={() => setOpenIndex(openIndex === index ? null : index)}
                className="flex w-full items-center justify-between px-6 py-4 text-left transition-colors hover:bg-muted/50"
              >
                <span className="font-medium text-text-primary">
                  {faq.question}
                </span>
                <ChevronDown
                  className={`h-5 w-5 shrink-0 text-text-secondary transition-transform ${
                    openIndex === index ? "rotate-180" : ""
                  }`}
                />
              </button>
              {openIndex === index && (
                <div className="border-t border-border px-6 py-4">
                  <p className="text-sm leading-relaxed text-text-secondary">
                    {faq.answer}
                  </p>
                </div>
              )}
            </div>
          ))}
        </div>
      </div>
    </section>
  )
}
