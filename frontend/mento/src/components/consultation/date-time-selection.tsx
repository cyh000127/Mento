import { useState, useMemo, useEffect, useRef } from "react"
import { ChevronLeft, ChevronRight, ArrowLeft, ArrowRight, Droplets, Sparkles, Scissors } from "lucide-react"
import type { ConsultationCategory } from "@/types/consultation"
import { getMonthlyTimetablesByMentorType } from "@/api/timetableApi"
import type { MonthlyTimetablesData, TimetableSlot } from "@/types/timetable"

interface DateTimeSelectionProps {
  selectedDate: Date | null
  selectedTime: string | null
  selectedCategory: ConsultationCategory | null
  onDateSelect: (date: Date | null) => void
  onTimeSelect: (time: string) => void
  onNext: () => void
  onPrev: () => void
  canProceed: boolean
}

const categoryLabels: Record<NonNullable<ConsultationCategory>, { label: string; icon: typeof Droplets }> = {
  skincare: { label: "스킨 케어", icon: Droplets },
  beauty: { label: "뷰티", icon: Sparkles },
  hair: { label: "헤어", icon: Scissors },
}

const categoryTypeIds: Record<NonNullable<ConsultationCategory>, number> = {
  skincare: 1,
  beauty: 2,
  hair: 3,
}

const DAYS_KO = ["일", "월", "화", "수", "목", "금", "토"]

const morningTimes = ["09:00", "10:00", "11:00", "12:00"]
const afternoonTimes = ["13:00", "14:00", "15:00", "16:00", "17:00"]

export function DateTimeSelection({
  selectedDate,
  selectedTime,
  selectedCategory,
  onDateSelect,
  onTimeSelect,
  onNext,
  onPrev,
  canProceed,
}: DateTimeSelectionProps) {
  const today = new Date()
  const [currentMonth, setCurrentMonth] = useState(today.getMonth())
  const [currentYear, setCurrentYear] = useState(today.getFullYear())
  const [timetable, setTimetable] = useState<MonthlyTimetablesData | null>(null)
  const inFlightTypeId = useRef<number | null>(null)
  const inFlightPromise = useRef<Promise<MonthlyTimetablesData> | null>(null)

  const categoryInfo = selectedCategory ? categoryLabels[selectedCategory] : null
  const CategoryIcon = categoryInfo?.icon

  const daysInMonth = useMemo(() => {
    const firstDay = new Date(currentYear, currentMonth, 1)
    const lastDay = new Date(currentYear, currentMonth + 1, 0)
    const daysCount = lastDay.getDate()
    const startingDay = firstDay.getDay()

    const days: (Date | null)[] = []

    // Previous month's trailing days
    for (let i = 0; i < startingDay; i++) {
      const prevMonthDay = new Date(currentYear, currentMonth, -startingDay + i + 1)
      days.push(prevMonthDay)
    }

    // Current month's days
    for (let i = 1; i <= daysCount; i++) {
      days.push(new Date(currentYear, currentMonth, i))
    }

    return days
  }, [currentMonth, currentYear])

  useEffect(() => {
    let isActive = true
    const typeId = selectedCategory ? categoryTypeIds[selectedCategory] : null

    if (!typeId) {
      setTimetable(null)
      inFlightTypeId.current = null
      inFlightPromise.current = null
      return () => {
        isActive = false
      }
    }

    if (inFlightTypeId.current !== typeId || !inFlightPromise.current) {
      inFlightTypeId.current = typeId
      inFlightPromise.current = getMonthlyTimetablesByMentorType(typeId)
    }

    inFlightPromise.current
      .then((data) => {
        if (isActive) {
          setTimetable(data)
        }
      })
      .catch((error) => {
        console.error("월간 타임테이블 조회 실패:", error)
        if (isActive) {
          setTimetable(null)
        }
      })
      .finally(() => {
        if (inFlightTypeId.current === typeId) {
          inFlightPromise.current = null
        }
      })

    return () => {
      isActive = false
    }
  }, [selectedCategory])

  const resetSelectedTime = () => {
    onTimeSelect("")
  }

  const resetSelectedDate = () => {
    onDateSelect(null)
  }

  const prevMonth = () => {
    resetSelectedTime()
    resetSelectedDate()
    if (currentMonth === 0) {
      setCurrentMonth(11)
      setCurrentYear((y) => y - 1)
    } else {
      setCurrentMonth((m) => m - 1)
    }
  }

  const nextMonth = () => {
    resetSelectedTime()
    resetSelectedDate()
    if (currentMonth === 11) {
      setCurrentMonth(0)
      setCurrentYear((y) => y + 1)
    } else {
      setCurrentMonth((m) => m + 1)
    }
  }

  const toDateOnly = (date: Date) =>
    new Date(date.getFullYear(), date.getMonth(), date.getDate())

  const toDateKey = (date: Date) => {
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, "0")
    const day = String(date.getDate()).padStart(2, "0")
    return `${year}-${month}-${day}`
  }

  const getSlotsByDate = (date: Date): TimetableSlot[] | null => {
    if (!timetable) return null
    const dateKey = toDateKey(date)
    return timetable.dailyTimetables.find((daily) => daily.date === dateKey)?.slots ?? null
  }

  const hasAvailableSlot = (slots: TimetableSlot[] | null) => {
    if (!slots) return false
    return slots.some((slot) => slot.status === "AVAILABLE" && slot.availableCapacity > 0)
  }

  const isWithinTimetableRange = (date: Date) => {
    if (!timetable) return false
    const dateKey = toDateKey(date)
    return dateKey >= timetable.startDate && dateKey <= timetable.endDate
  }

  const isDateDisabled = (date: Date) => {
    const todayDate = toDateOnly(new Date())
    const targetDate = toDateOnly(date)
    const isPast = targetDate < todayDate
    const slots = getSlotsByDate(date)
    const isOutsideRange = !isWithinTimetableRange(date)
    const isBooked = isWithinTimetableRange(date) && !hasAvailableSlot(slots)

    return isPast || isOutsideRange || isBooked
  }

  const isToday = (date: Date) => {
    return (
      date.getDate() === today.getDate() &&
      date.getMonth() === today.getMonth() &&
      date.getFullYear() === today.getFullYear()
    )
  }

  const isSameDay = (date1: Date | null, date2: Date | null) => {
    if (!date1 || !date2) return false
    return (
      date1.getDate() === date2.getDate() &&
      date1.getMonth() === date2.getMonth() &&
      date1.getFullYear() === date2.getFullYear()
    )
  }

  const isCurrentMonth = (date: Date) => {
    return date.getMonth() === currentMonth
  }

  if (!selectedCategory) {
    return null
  }

  const selectedDateSlots = selectedDate ? getSlotsByDate(selectedDate) : null
  const normalizeScheduledTime = (time: string) => time.slice(0, 5)

  const isTimeUnavailable = (time: string) => {
    if (!selectedDateSlots) return true
    const slot = selectedDateSlots.find(
      (candidate) => normalizeScheduledTime(candidate.scheduledTime) === time
    )
    return !slot || slot.status !== "AVAILABLE" || slot.availableCapacity <= 0
  }

  return (
    <div className="flex flex-col">
      {/* Header */}
      <div className="flex items-center gap-4">
        {categoryInfo && CategoryIcon && (
          <div className="flex items-center gap-2 rounded-lg bg-primary-100 px-4 py-2">
            <CategoryIcon className="h-5 w-5 text-primary-500" />
            <span className="font-semibold text-text-primary">{categoryInfo.label}</span>
          </div>
        )}
        <h1 className="text-2xl font-bold text-text-primary">
          상담 받을 날짜와 시간을 선택해 주세요
        </h1>
      </div>

      {/* Main Content */}
      <div className="mt-8 grid grid-cols-1 gap-8 lg:grid-cols-2">
        {/* Calendar */}
        <div className="rounded-2xl border border-border bg-card p-6 shadow-sm">
          {/* Month Navigation */}
          <div className="mb-6 flex items-center justify-between">
            <button
              type="button"
              onClick={prevMonth}
              className="rounded-lg p-2 text-text-secondary transition-colors hover:bg-muted hover:text-text-primary"
            >
              <ChevronLeft className="h-5 w-5" />
            </button>
            <h2 className="text-lg font-bold text-text-primary">
              {currentYear}.{currentMonth + 1}
            </h2>
            <button
              type="button"
              onClick={nextMonth}
              className="rounded-lg p-2 text-text-secondary transition-colors hover:bg-muted hover:text-text-primary"
            >
              <ChevronRight className="h-5 w-5" />
            </button>
          </div>

          {/* Day Headers */}
          <div className="mb-2 grid grid-cols-7 gap-1">
            {DAYS_KO.map((day, i) => (
              <div
                key={day}
                className={`py-2 text-center text-sm font-medium ${
                  i === 0 ? "text-red-400" : i === 6 ? "text-blue-400" : "text-text-secondary"
                }`}
              >
                {day}
              </div>
            ))}
          </div>

          {/* Calendar Grid */}
          <div className="grid grid-cols-7 gap-1">
            {daysInMonth.map((date, idx) => {
              if (!date) return <div key={`empty-${idx}`} />

              const disabled = isDateDisabled(date)
              const selected = isSameDay(date, selectedDate)
              const todayDate = isToday(date)
              const inCurrentMonth = isCurrentMonth(date)
              const slots = getSlotsByDate(date)
              const isBooked =
                isWithinTimetableRange(date) && !hasAvailableSlot(slots)
              const dayOfWeek = date.getDay()

              return (
                <button
                  key={date.toISOString()}
                  type="button"
                  onClick={() => {
                    if (disabled) return
                    onDateSelect(date)
                    resetSelectedTime()
                  }}
                  disabled={disabled}
                  className={`relative flex h-12 flex-col items-center justify-center rounded-lg text-sm transition-all
                    ${
                      disabled
                        ? "cursor-not-allowed bg-transparent text-gray-300"
                        : selected
                          ? "bg-primary-500 font-semibold text-dark-bg shadow-md"
                          : !inCurrentMonth
                            ? "bg-transparent text-text-secondary/40"
                            : dayOfWeek === 0
                              ? "text-red-400 hover:bg-muted"
                              : dayOfWeek === 6
                                ? "text-blue-400 hover:bg-muted"
                                : "text-text-primary hover:bg-muted"
                    }
                  `}
                >
                  <span>{date.getDate()}</span>
                  {todayDate && inCurrentMonth && (
                    <span className={`text-[10px] ${selected ? "text-dark-bg/80" : "text-primary-500"}`}>
                      오늘
                    </span>
                  )}
                  {isBooked && inCurrentMonth && !todayDate && (
                    <span className="text-[10px] text-text-secondary/60">마감</span>
                  )}
                </button>
              )
            })}
          </div>
        </div>

        {/* Time Selection */}
        <div className="rounded-2xl border border-border bg-card p-6 shadow-sm">
          <h3 className="mb-6 text-lg font-bold text-text-primary">시간 선택</h3>

          {/* Morning */}
          <div className="mb-6">
            <h4 className="mb-3 text-sm font-medium text-text-secondary">오전</h4>
            <div className="grid grid-cols-4 gap-2">
              {morningTimes.map((time) => {
                const isUnavailable = isTimeUnavailable(time)
                const isSelected = selectedTime === time

                return (
                  <button
                    key={time}
                    type="button"
                    onClick={() => !isUnavailable && onTimeSelect(time)}
                    disabled={isUnavailable}
                    className={`rounded-lg border px-3 py-2.5 text-sm font-medium transition-all ${
                      isSelected
                        ? "border-primary-500 bg-primary-500 text-dark-bg shadow-md"
                        : isUnavailable
                          ? "cursor-not-allowed border-border bg-muted/50 text-text-secondary/40"
                          : "border-border bg-background text-text-primary hover:border-primary-300 hover:bg-primary-100"
                    }`}
                  >
                    {time}
                  </button>
                )
              })}
            </div>
          </div>

          {/* Afternoon */}
          <div>
            <h4 className="mb-3 text-sm font-medium text-text-secondary">오후</h4>
            <div className="grid grid-cols-4 gap-2">
              {afternoonTimes.map((time) => {
                const isUnavailable = isTimeUnavailable(time)
                const isSelected = selectedTime === time

                return (
                  <button
                    key={time}
                    type="button"
                    onClick={() => !isUnavailable && onTimeSelect(time)}
                    disabled={isUnavailable}
                    className={`rounded-lg border px-3 py-2.5 text-sm font-medium transition-all ${
                      isSelected
                        ? "border-primary-500 bg-primary-500 text-dark-bg shadow-md"
                        : isUnavailable
                          ? "cursor-not-allowed border-border bg-muted/50 text-text-secondary/40"
                          : "border-border bg-background text-text-primary hover:border-primary-300 hover:bg-primary-100"
                    }`}
                  >
                    {time}
                  </button>
                )
              })}
            </div>
          </div>
        </div>
      </div>

      {/* Navigation Buttons */}
      <div className="mt-12 flex items-center justify-between">
        <button
          type="button"
          onClick={() => {
            resetSelectedTime()
            resetSelectedDate()
            onPrev()
          }}
          className="flex items-center gap-2 rounded-xl border border-border px-6 py-3 text-base font-semibold text-text-primary transition-colors hover:bg-muted"
        >
          <ArrowLeft className="h-5 w-5" />
          이전 단계
        </button>

        <button
          type="button"
          onClick={onNext}
          disabled={!canProceed}
          className={`flex items-center gap-2 rounded-xl px-8 py-3 text-base font-semibold transition-all ${
            canProceed
              ? "bg-primary-500 text-dark-bg hover:bg-primary-400 shadow-lg shadow-primary-500/30"
              : "cursor-not-allowed bg-muted text-muted-foreground"
          }`}
        >
          다음 단계
          <ArrowRight className="h-5 w-5" />
        </button>
      </div>
    </div>
  )
}
