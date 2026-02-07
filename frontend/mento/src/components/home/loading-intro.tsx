import { useEffect, useRef, useState } from "react"

interface LoadingIntroProps {
  onComplete: () => void
}

export function LoadingIntro({ onComplete }: LoadingIntroProps) {
  const [phase, setPhase] = useState<"question" | "counting" | "complete" | "logo">("question")
  const [count, setCount] = useState(0)
  const [speed, setSpeed] = useState(1)
  const completedRef = useRef(false)
  const onCompleteRef = useRef(onComplete)

  useEffect(() => {
    onCompleteRef.current = onComplete
  }, [onComplete])

  useEffect(() => {
    let isActive = true
    const timeouts: number[] = []
    let intervalId: number | null = null

    const duration = 2000 // 2초
    const steps = 99
    let currentCount = 0

    // 패턴 스킵
    const showNumbers = [
      0, 1, 3, 7, 12, 19, 28, 38, 49, 61, 74, 85, 92, 96, 97, 98, 99
    ]

    const finishIntro = () => {
      if (completedRef.current) return
      completedRef.current = true
      onCompleteRef.current()
    }

    const startCounting = () => {
      intervalId = window.setInterval(() => {
        if (!isActive) return
        currentCount++

        // 특정 숫자만 표시
        if (showNumbers.includes(currentCount) || currentCount >= 95) {
          setCount(currentCount)
        }

        // 속도 증가
        const speedMultiplier = 1 + (currentCount / steps) * 3
        setSpeed(speedMultiplier)

        if (currentCount >= steps) {
          if (intervalId) {
            clearInterval(intervalId)
            intervalId = null
          }

          // "complete" 메시지 표시
          const completeTimer = window.setTimeout(() => {
            if (!isActive) return
            setPhase("complete")

            // MENTO 로고 표시
            const logoTimer = window.setTimeout(() => {
              if (!isActive) return
              setPhase("logo")

              // MENTO 표시 후 완료
              const doneTimer = window.setTimeout(() => {
                if (!isActive) return
                finishIntro()
              }, 1200)
              timeouts.push(doneTimer)
            }, 1500)
            timeouts.push(logoTimer)
          }, 300)
          timeouts.push(completeTimer)
        }
      }, duration / steps)
    }

    // 단계 1: 질문 표시 (1.5초)
    const questionTimer = window.setTimeout(() => {
      if (!isActive) return
      setPhase("counting")
      startCounting()
    }, 1500)
    timeouts.push(questionTimer)

    return () => {
      isActive = false
      timeouts.forEach((timerId) => clearTimeout(timerId))
      if (intervalId) {
        clearInterval(intervalId)
        intervalId = null
      }
    }
  }, [])

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center bg-background">
      {/* 중앙 콘텐츠 */}
      <div className="relative flex flex-col items-center justify-center px-8">
        {phase === "question" && (
          // 질문 단계
          <div className="animate-fade-in text-center">
            <h2 className="text-3xl font-bold leading-tight text-text-primary md:text-4xl lg:text-5xl">
              평소에 얼마나
              <br />
              <span className="bg-gradient-to-r from-primary-500 to-primary-300 bg-clip-text text-transparent">
                꾸미고 싶나요?
              </span>
            </h2>
          </div>
        )}

        {phase === "counting" && (
          // 카운터 표시
          <div className="animate-fade-in text-center">
            <div className="relative">
              <div
                className="text-[12rem] font-bold leading-none tracking-tighter text-text-primary md:text-[16rem] lg:text-[20rem]"
                style={{
                  fontVariantNumeric: "tabular-nums",
                }}
              >
                {count.toString().padStart(2, "0")}
              </div>
              {/* 펄스 효과 */}
              <div className="absolute inset-0 flex items-center justify-center">
                <div
                  className="h-full w-full animate-ping rounded-full bg-primary-500/20"
                  style={{
                    animationDuration: `${1 / speed}s`,
                  }}
                />
              </div>
            </div>
          </div>
        )}

        {phase === "complete" && (
          // 완료 메시지
          <div className="animate-scale-in text-center">
            <h2 className="text-3xl font-bold leading-tight text-text-primary md:text-4xl lg:text-5xl">
              <span className="bg-gradient-to-r from-primary-500 to-primary-300 bg-clip-text text-transparent">
                이 정도 시간이면
              </span>
              <br />
              충분합니다!
            </h2>
          </div>
        )}

        {phase === "logo" && (
          // MENTO 로고
          <div className="animate-scale-in text-center">
            <h1 className="mb-4 text-7xl font-bold tracking-tight text-text-primary md:text-8xl lg:text-9xl">
              MENTO
            </h1>
            <div className="mx-auto h-1 w-32 bg-gradient-to-r from-transparent via-primary-500 to-transparent" />
            <p className="mt-6 text-base font-medium text-text-secondary md:text-lg">
              당신의 뷰티 파트너
            </p>
          </div>
        )}
      </div>

      {/* 진행 표시 */}
      {(phase === "question" || phase === "counting") && (
        <div className="absolute bottom-12 left-1/2 -translate-x-1/2">
          <div className="flex items-center gap-2">
            <div className="flex gap-1">
              <span className={`h-2 w-2 rounded-full ${phase === "question" ? "bg-primary-500" : "bg-primary-500/30"}`} />
              <span className={`h-2 w-2 rounded-full ${phase === "counting" ? "bg-primary-500 animate-pulse" : "bg-primary-500/30"}`} />
              <span className="h-2 w-2 rounded-full bg-primary-500/30" />
              <span className="h-2 w-2 rounded-full bg-primary-500/30" />
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
