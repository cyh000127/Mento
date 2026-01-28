import { useEffect, useState } from "react"

interface LoadingIntroProps {
  onComplete: () => void
}

export function LoadingIntro({ onComplete }: LoadingIntroProps) {
  const [phase, setPhase] = useState<"question" | "counting" | "complete" | "logo">("question")
  const [count, setCount] = useState(0)
  const [speed, setSpeed] = useState(1)

  useEffect(() => {
    // Phase 1: Show question for 1.5 seconds
    const questionTimer = setTimeout(() => {
      setPhase("counting")
    }, 1500)

    return () => clearTimeout(questionTimer)
  }, [])

  useEffect(() => {
    if (phase !== "counting") return

    // Counter animation (0 to 99) with skipping - faster
    const duration = 2000 // 2 seconds (faster)
    const steps = 99
    let currentCount = 0

    // Skip pattern: show some numbers, skip others (more aggressive skipping)
    const showNumbers = [
      0, 1, 3, 7, 12, 19, 28, 38, 49, 61, 74, 85, 92, 96, 97, 98, 99
    ]

    const animate = () => {
      const interval = setInterval(() => {
        currentCount++
        
        // Only update display for certain numbers
        if (showNumbers.includes(currentCount) || currentCount >= 95) {
          setCount(currentCount)
        }

        // Gradually increase speed
        const speedMultiplier = 1 + (currentCount / steps) * 3
        setSpeed(speedMultiplier)

        if (currentCount >= steps) {
          clearInterval(interval)
          // Show "complete" message
          setTimeout(() => {
            setPhase("complete")
            // Show MENTO logo
            setTimeout(() => {
              setPhase("logo")
              // Complete after showing MENTO
              setTimeout(() => {
                onComplete()
              }, 1200)
            }, 1500)
          }, 300)
        }
      }, duration / steps)

      return interval
    }

    const interval = animate()
    return () => clearInterval(interval)
  }, [phase, onComplete])

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center bg-background">
      {/* Center Content */}
      <div className="relative flex flex-col items-center justify-center px-8">
        {phase === "question" && (
          // Question phase
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
          // Counter display
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
              {/* Pulse effect */}
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
          // Complete message
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
          // MENTO logo
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

      {/* Progress indicator */}
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
