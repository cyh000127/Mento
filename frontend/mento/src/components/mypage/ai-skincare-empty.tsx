import { Sparkles } from "lucide-react"
import { Button } from "@/components/ui/button"
import { useNavigate } from "react-router-dom"

interface AiSkincareEmptyProps {
  onStartDiagnosis?: () => void
}

export function AiSkincareEmpty({ onStartDiagnosis }: AiSkincareEmptyProps) {
  const navigate = useNavigate()

  const handleStartDiagnosis = () => {
    if (onStartDiagnosis) {
      onStartDiagnosis()
    }
    navigate("/ai-care")
  }

  return (
    <div className="flex min-h-[400px] items-center justify-center rounded-xl border border-border bg-card p-12 shadow-sm">
      <div className="flex max-w-md flex-col items-center text-center">
        {/* Icon */}
        <div className="mb-6 flex h-20 w-20 items-center justify-center rounded-full bg-muted">
          <Sparkles className="h-10 w-10 text-muted-foreground" />
        </div>

        {/* Title */}
        <h3 className="mb-3 text-xl font-bold text-foreground">
          AI 피부 진단 이력이 없습니다
        </h3>

        {/* Description */}
        <p className="mb-6 text-sm leading-relaxed text-muted-foreground">
          아직 AI 피부 진단을 받은 이력이 없어요.
          <br />
          AI 기반 피부 분석으로 맞춤형 스킨케어 솔루션을 받아보세요.
        </p>

        {/* CTA Button */}
        <Button
          onClick={handleStartDiagnosis}
          className="bg-primary-500 text-dark-bg hover:bg-primary-400"
        >
          AI 피부 진단 시작하기
        </Button>
      </div>
    </div>
  )
}
