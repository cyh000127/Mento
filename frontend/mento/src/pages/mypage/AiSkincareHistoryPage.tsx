import { useState, useMemo } from "react"
import { MyPageSidebar } from "@/components/mypage/mypage-sidebar"
import { PeriodDateFilters } from "@/components/mypage/consultation-filters"
import { AiSkincareList } from "@/components/mypage/ai-skincare-list"
import { AiSkincareEmpty } from "@/components/mypage/ai-skincare-empty"
import type { AiSkincareDiagnosis, PeriodFilter } from "@/types/ai-skincare"

// Mock data for demonstration
const mockDiagnoses: AiSkincareDiagnosis[] = [
  {
    id: "1",
    diagnosisDate: "2026-01-20",
    skinType: "복합성",
    concerns: ["모공", "피지"],
    recommendations: ["세안 후 즉시 보습", "주 2회 각질 제거"],
  },
  {
    id: "2",
    diagnosisDate: "2026-01-20",
    skinType: "건성",
    concerns: ["건조", "각질"],
    recommendations: ["보습 크림 사용", "수분 공급 강화"],
  },
  {
    id: "3",
    diagnosisDate: "2026-01-20",
    skinType: "지성",
    concerns: ["여드름", "모공"],
    recommendations: ["저자극 세안제 사용", "유분기 없는 제품 사용"],
  },
  {
    id: "4",
    diagnosisDate: "2026-01-20",
    skinType: "민감성",
    concerns: ["홍조", "트러블"],
    recommendations: ["순한 성분 제품 사용", "자극 최소화"],
  },
  {
    id: "5",
    diagnosisDate: "2026-01-20",
    skinType: "정상",
    concerns: ["기미", "주름"],
    recommendations: ["자외선 차단", "항산화 제품 사용"],
  },
]

export default function AiSkincareHistoryPage() {
  // Filter states
  const [selectedPeriod, setSelectedPeriod] = useState<PeriodFilter>("1month")
  const [startYear, setStartYear] = useState("2025")
  const [startMonth, setStartMonth] = useState("12")
  const [startDay, setStartDay] = useState("20")
  const [endYear, setEndYear] = useState("2026")
  const [endMonth, setEndMonth] = useState("1")
  const [endDay, setEndDay] = useState("20")
  const [isSearched, setIsSearched] = useState(true)

  // Data state
  const [diagnoses] = useState<AiSkincareDiagnosis[]>(mockDiagnoses)

  // Initialize dates on period change
  const handlePeriodChange = (period: PeriodFilter) => {
    setSelectedPeriod(period)
    const today = new Date()
    const start = new Date()

    switch (period) {
      case "1month":
        start.setMonth(today.getMonth() - 1)
        break
      case "3months":
        start.setMonth(today.getMonth() - 3)
        break
      case "6months":
        start.setMonth(today.getMonth() - 6)
        break
      case "12months":
        start.setFullYear(today.getFullYear() - 1)
        break
    }

    setStartYear(start.getFullYear().toString())
    setStartMonth((start.getMonth() + 1).toString())
    setStartDay(start.getDate().toString())
    setEndYear(today.getFullYear().toString())
    setEndMonth((today.getMonth() + 1).toString())
    setEndDay(today.getDate().toString())
  }

  // Calculate date range based on dropdowns
  const dateRange = useMemo(() => {
    return {
      start: `${startYear}-${startMonth.padStart(2, "0")}-${startDay.padStart(2, "0")}`,
      end: `${endYear}-${endMonth.padStart(2, "0")}-${endDay.padStart(2, "0")}`,
    }
  }, [startYear, startMonth, startDay, endYear, endMonth, endDay])

  // Filter diagnoses (only when search is clicked)
  const filteredDiagnoses = useMemo(() => {
    if (!isSearched) return []

    return diagnoses.filter((diagnosis) => {
      // Date range filter
      const diagnosisDate = new Date(diagnosis.diagnosisDate)
      const rangeStart = new Date(dateRange.start)
      const rangeEnd = new Date(dateRange.end)

      if (diagnosisDate < rangeStart || diagnosisDate > rangeEnd) {
        return false
      }

      return true
    })
  }, [diagnoses, dateRange, isSearched])

  // Sort diagnoses by date (most recent first)
  const sortedDiagnoses = useMemo(() => {
    return [...filteredDiagnoses].sort((a, b) => {
      return new Date(b.diagnosisDate).getTime() - new Date(a.diagnosisDate).getTime()
    })
  }, [filteredDiagnoses])

  // Handlers
  const handleSearch = () => {
    setIsSearched(true)
  }

  const handleViewDetail = (diagnosis: AiSkincareDiagnosis) => {
    // In real app, this would navigate to detail page or show modal
    console.log("View diagnosis detail:", diagnosis)
  }

  const handleStartDiagnosis = () => {
    // Navigate to AI diagnosis page
    console.log("Navigate to AI diagnosis page")
  }

  return (
    <div className="flex min-h-screen bg-background justify-center">
      <div className="flex w-full max-w-[1200px]">
        <MyPageSidebar />
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-6 py-8">
            {/* Page Header */}
            <div className="pl-1">
              <h1 className="text-2xl font-bold text-foreground pb-3">
                AI 피부관리 내역
              </h1>
            </div>

            {/* Filters */}
            <div className="mb-6 rounded-xl border border-border bg-card p-6 shadow-sm">
              <PeriodDateFilters
                selectedPeriod={selectedPeriod}
                onPeriodChange={handlePeriodChange}
                startYear={startYear}
                onStartYearChange={setStartYear}
                startMonth={startMonth}
                onStartMonthChange={setStartMonth}
                startDay={startDay}
                onStartDayChange={setStartDay}
                endYear={endYear}
                onEndYearChange={setEndYear}
                endMonth={endMonth}
                onEndMonthChange={setEndMonth}
                endDay={endDay}
                onEndDayChange={setEndDay}
                onSearch={handleSearch}
              />
            </div>

            {/* Diagnosis List or Empty State */}
            {isSearched ? (
              sortedDiagnoses.length > 0 ? (
                <AiSkincareList
                  diagnoses={sortedDiagnoses}
                  onViewDetail={handleViewDetail}
                />
              ) : (
                <AiSkincareEmpty onStartDiagnosis={handleStartDiagnosis} />
              )
            ) : (
              <AiSkincareEmpty onStartDiagnosis={handleStartDiagnosis} />
            )}
          </div>
        </div>
      </div>
    </div>
  )
}
