import { Button } from "@/components/ui/button"
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select"
import type { PeriodFilter } from "@/types/consultation"

interface ConsultationFiltersProps {
  selectedPeriod: PeriodFilter
  onPeriodChange: (period: PeriodFilter) => void
  startYear: string
  onStartYearChange: (year: string) => void
  startMonth: string
  onStartMonthChange: (month: string) => void
  startDay: string
  onStartDayChange: (day: string) => void
  endYear: string
  onEndYearChange: (year: string) => void
  endMonth: string
  onEndMonthChange: (month: string) => void
  endDay: string
  onEndDayChange: (day: string) => void
  onSearch: () => void
}

const periodLabels: Record<PeriodFilter, string> = {
  "1month": "1개월",
  "3months": "3개월",
  "6months": "6개월",
  "12months": "12개월",
}

// Generate years (2020-2030)
const years = Array.from({ length: 11 }, (_, i) => (2020 + i).toString())
// Generate months (1-12)
const months = Array.from({ length: 12 }, (_, i) => (i + 1).toString())
// Generate days (1-31)
const days = Array.from({ length: 31 }, (_, i) => (i + 1).toString())

export function ConsultationFilters({
  selectedPeriod,
  onPeriodChange,
  startYear,
  onStartYearChange,
  startMonth,
  onStartMonthChange,
  startDay,
  onStartDayChange,
  endYear,
  onEndYearChange,
  endMonth,
  onEndMonthChange,
  endDay,
  onEndDayChange,
  onSearch,
}: ConsultationFiltersProps) {
  return (
    <div className="space-y-4">
      {/* Period Filter */}
      <div className="flex items-center gap-3">
        <span className="text-sm font-medium text-foreground">기간</span>
        <div className="flex flex-wrap gap-2">
          {(Object.keys(periodLabels) as PeriodFilter[]).map((period) => (
            <button
              key={period}
              onClick={() => onPeriodChange(period)}
              className={`rounded-lg px-4 py-2 text-sm font-medium transition-all ${
                selectedPeriod === period
                  ? "bg-dark-bg text-white"
                  : "bg-muted text-muted-foreground hover:bg-muted/80"
              }`}
            >
              {periodLabels[period]}
            </button>
          ))}
        </div>
      </div>

      {/* Date Range Picker */}
      <div className="flex flex-wrap items-center gap-3">
        {/* Start Date */}
        <div className="flex items-center gap-2">
          <Select value={startYear} onValueChange={onStartYearChange}>
            <SelectTrigger className="w-[100px]">
              <SelectValue placeholder="년" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {years.map((year) => (
                <SelectItem key={year} value={year}>
                  {year}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <span className="text-sm text-muted-foreground">년</span>

          <Select value={startMonth} onValueChange={onStartMonthChange}>
            <SelectTrigger className="w-[80px]">
              <SelectValue placeholder="월" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {months.map((month) => (
                <SelectItem key={month} value={month}>
                  {month}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <span className="text-sm text-muted-foreground">월</span>

          <Select value={startDay} onValueChange={onStartDayChange}>
            <SelectTrigger className="w-[80px]">
              <SelectValue placeholder="일" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {days.map((day) => (
                <SelectItem key={day} value={day}>
                  {day}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <span className="text-sm text-muted-foreground">일</span>
        </div>

        <span className="text-sm text-muted-foreground">~</span>

        {/* End Date */}
        <div className="flex items-center gap-2">
          <Select value={endYear} onValueChange={onEndYearChange}>
            <SelectTrigger className="w-[100px]">
              <SelectValue placeholder="년" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {years.map((year) => (
                <SelectItem key={year} value={year}>
                  {year}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <span className="text-sm text-muted-foreground">년</span>

          <Select value={endMonth} onValueChange={onEndMonthChange}>
            <SelectTrigger className="w-[80px]">
              <SelectValue placeholder="월" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {months.map((month) => (
                <SelectItem key={month} value={month}>
                  {month}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <span className="text-sm text-muted-foreground">월</span>

          <Select value={endDay} onValueChange={onEndDayChange}>
            <SelectTrigger className="w-[80px]">
              <SelectValue placeholder="일" />
            </SelectTrigger>
            <SelectContent className="bg-white">
              {days.map((day) => (
                <SelectItem key={day} value={day}>
                  {day}
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
          <span className="text-sm text-muted-foreground">일</span>
        </div>

        {/* Search Button */}
        <Button
          onClick={onSearch}
          className="bg-dark-bg text-white hover:bg-dark-bg/90"
        >
          조회
        </Button>
      </div>
    </div>
  )
}
