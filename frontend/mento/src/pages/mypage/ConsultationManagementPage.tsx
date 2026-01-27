import { useState, useMemo } from "react"
import { MyPageSidebar } from "@/components/mypage/mypage-sidebar"
import { ConsultationFilters } from "@/components/mypage/consultation-filters"
import { ConsultationCategoryFilter } from "@/components/mypage/consultation-category-filter"
import { ConsultationList } from "@/components/mypage/consultation-list"
import { ConsultationEmpty } from "@/components/mypage/consultation-empty"
import { ConsultationDetail } from "@/components/mypage/consultation-detail"
import type {
  Consultation,
  ConsultationCategory,
  PeriodFilter,
} from "@/types/consultation"

// Mock data for demonstration
const mockConsultations: Consultation[] = [
  {
    id: "1",
    category: "skincare",
    scheduledDate: "2024-02-15",
    scheduledTime: "14:00",
    status: "scheduled",
    expertName: "김민수",
    expertTitle: "피부과 전문의",
    roomUrl: "https://example.com/room/1",
    memo: "피부 건조증과 트러블 개선을 위한 상담입니다.",
    preConsultationQA: [
      {
        question: "현재 피부 고민이 무엇인가요?",
        answer: "T존 부위의 과도한 유분과 볼 부위의 건조함이 심합니다.",
      },
      {
        question: "현재 사용 중인 제품이 있나요?",
        answer: "아침에는 세안 후 토너와 보습제만 사용하고 있습니다.",
      },
    ],
  },
  {
    id: "2",
    category: "beauty",
    scheduledDate: "2024-01-28",
    scheduledTime: "15:30",
    status: "completed",
    expertName: "이지은",
    expertTitle: "메이크업 아티스트",
    memo: "웨딩 메이크업 컬러 및 스타일 상담 완료",
    preConsultationQA: [
      {
        question: "원하시는 메이크업 스타일이 있나요?",
        answer: "자연스러우면서도 화사한 웨딩 메이크업을 원합니다.",
      },
    ],
  },
  {
    id: "3",
    category: "hair",
    scheduledDate: "2024-01-10",
    scheduledTime: "11:00",
    status: "completed",
    expertName: "박준형",
    expertTitle: "헤어 디자이너",
    memo: "두피 케어 및 탈모 예방 상담",
  },
  {
    id: "4",
    category: "skincare",
    scheduledDate: "2023-12-20",
    scheduledTime: "16:00",
    status: "cancelled",
    expertName: "최수진",
    expertTitle: "피부 관리사",
    memo: "일정 변경으로 인한 취소",
  },
]

export default function ConsultationManagementPage() {
  // View state
  const [selectedConsultation, setSelectedConsultation] = useState<Consultation | null>(null)

  // Filter states
  const [selectedPeriod, setSelectedPeriod] = useState<PeriodFilter>("1month")
  const [startYear, setStartYear] = useState("2025")
  const [startMonth, setStartMonth] = useState("12")
  const [startDay, setStartDay] = useState("20")
  const [endYear, setEndYear] = useState("2026")
  const [endMonth, setEndMonth] = useState("1")
  const [endDay, setEndDay] = useState("20")
  const [selectedCategory, setSelectedCategory] = useState<ConsultationCategory | "all">("all")
  const [isSearched, setIsSearched] = useState(false)

  // Data state
  const [consultations] = useState<Consultation[]>(mockConsultations)

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

  // Filter consultations (only when search is clicked)
  const filteredConsultations = useMemo(() => {
    if (!isSearched) return []

    return consultations.filter((consultation) => {
      // Date range filter
      const consultationDate = new Date(consultation.scheduledDate)
      const rangeStart = new Date(dateRange.start)
      const rangeEnd = new Date(dateRange.end)

      if (consultationDate < rangeStart || consultationDate > rangeEnd) {
        return false
      }

      // Category filter
      if (selectedCategory !== "all" && consultation.category !== selectedCategory) {
        return false
      }

      return true
    })
  }, [consultations, dateRange, selectedCategory, isSearched])

  // Sort consultations by date (most recent first)
  const sortedConsultations = useMemo(() => {
    return [...filteredConsultations].sort((a, b) => {
      return new Date(b.scheduledDate).getTime() - new Date(a.scheduledDate).getTime()
    })
  }, [filteredConsultations])

  // Handlers
  const handleSearch = () => {
    setIsSearched(true)
  }

  const handleViewDetail = (consultation: Consultation) => {
    setSelectedConsultation(consultation)
  }

  const handleBackToList = () => {
    setSelectedConsultation(null)
  }

  const handleCancelConsultation = (consultationId: string) => {
    // In real app, this would call API to cancel consultation
    console.log("Cancel consultation:", consultationId)
  }

  const handleEnterRoom = (roomUrl: string) => {
    // In real app, this would navigate to video consultation room
    console.log("Enter consultation room:", roomUrl)
    window.open(roomUrl, "_blank")
  }

  const handleBookConsultation = () => {
    // Navigate to consultation booking page
    console.log("Navigate to booking page")
  }

  // Show detail view if consultation is selected
  if (selectedConsultation) {
    return (
      <div className="flex min-h-screen bg-background justify-center">
        <div className="flex w-full max-w-[1200px]">
          <MyPageSidebar />
          <div className="flex-1">
            <ConsultationDetail
              consultation={selectedConsultation}
              onBack={handleBackToList}
            />
          </div>
        </div>
      </div>
    )
  }

  return (
    <div className="flex min-h-screen bg-background justify-center">
      <div className="flex w-full max-w-[1200px]">
        <MyPageSidebar />
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-6 py-8">
          {/* Page Header */}
          <div className="mb-8">
            <h1 className="text-2xl font-bold text-foreground border-b border-border pb-3">
              상담 관리
            </h1>
          </div>

          {/* Filters */}
          <div className="mb-6 rounded-xl border border-border bg-card p-6 shadow-sm">
            <ConsultationFilters
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

            <div className="mt-6 pt-6 border-t border-border">
              <ConsultationCategoryFilter
                selectedCategory={selectedCategory}
                onCategoryChange={setSelectedCategory}
              />
            </div>
          </div>

          {/* Consultation List or Empty State */}
          {isSearched ? (
            sortedConsultations.length > 0 ? (
              <ConsultationList
                consultations={sortedConsultations}
                onViewDetail={handleViewDetail}
                onCancelConsultation={handleCancelConsultation}
                onEnterRoom={handleEnterRoom}
              />
            ) : (
              <ConsultationEmpty onBookConsultation={handleBookConsultation} />
            )
          ) : (
            <ConsultationEmpty onBookConsultation={handleBookConsultation} />
          )}
        </div>
        </div>
      </div>
    </div>
  )
}
