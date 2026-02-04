import { useEffect, useMemo, useRef, useState } from "react";
import { MyPageSidebar } from "@/components/mypage/mypage-sidebar";
import { PeriodDateFilters } from "@/components/mypage/consultation-filters";
import { AiSkincareList } from "@/components/mypage/ai-skincare-list";
import { AiSkincareEmpty } from "@/components/mypage/ai-skincare-empty";
import type { AiSkincareDiagnosis, PeriodFilter } from "@/types/ai-skincare";
import { getSkinAnalysisHistory } from "@/api/skinAnalysisApi";

export default function AiSkincareHistoryPage() {
  // Filter states
  const [selectedPeriod, setSelectedPeriod] = useState<PeriodFilter>("1month");
  const [startYear, setStartYear] = useState("");
  const [startMonth, setStartMonth] = useState("");
  const [startDay, setStartDay] = useState("");
  const [endYear, setEndYear] = useState("");
  const [endMonth, setEndMonth] = useState("");
  const [endDay, setEndDay] = useState("");
  const [isSearched, setIsSearched] = useState(false);
  const [searchParams, setSearchParams] = useState<{
    startDate: string;
    endDate: string;
  } | null>(null);

  // Data state
  const [diagnoses, setDiagnoses] = useState<AiSkincareDiagnosis[]>([]);
  const hasFetchedRef = useRef(false);

  useEffect(() => {
    if (hasFetchedRef.current) return;
    hasFetchedRef.current = true;

    const fetchDiagnoses = async () => {
      const response = await getSkinAnalysisHistory();
      setDiagnoses(response.content);
    };

    void fetchDiagnoses();
  }, []);

  // Initialize dates on period change
  const handlePeriodChange = (period: PeriodFilter) => {
    setSelectedPeriod(period);
    const today = new Date();
    const start = new Date();

    switch (period) {
      case "1month":
        start.setMonth(today.getMonth() - 1);
        break;
      case "3months":
        start.setMonth(today.getMonth() - 3);
        break;
      case "6months":
        start.setMonth(today.getMonth() - 6);
        break;
      case "12months":
        start.setFullYear(today.getFullYear() - 1);
        break;
    }

    setStartYear(start.getFullYear().toString());
    setStartMonth((start.getMonth() + 1).toString());
    setStartDay(start.getDate().toString());
    setEndYear(today.getFullYear().toString());
    setEndMonth((today.getMonth() + 1).toString());
    setEndDay(today.getDate().toString());
  };

  // Calculate date range based on dropdowns
  const dateRange = useMemo(() => {
    if (!startYear || !startMonth || !startDay || !endYear || !endMonth || !endDay) {
      return { start: "", end: "" };
    }

    return {
      start: `${startYear}-${startMonth.padStart(2, "0")}-${startDay.padStart(2, "0")}`,
      end: `${endYear}-${endMonth.padStart(2, "0")}-${endDay.padStart(2, "0")}`,
    };
  }, [startYear, startMonth, startDay, endYear, endMonth, endDay]);

  const filteredDiagnoses = useMemo(() => {
    if (!isSearched || !searchParams) {
      return [];
    }

    const rangeStart = new Date(searchParams.startDate);
    const rangeEnd = new Date(searchParams.endDate);

    return diagnoses.filter((diagnosis) => {
      const diagnosisDate = new Date(diagnosis.created_at);
      if (Number.isNaN(diagnosisDate.getTime())) {
        return false;
      }

      return diagnosisDate >= rangeStart && diagnosisDate <= rangeEnd;
    });
  }, [diagnoses, isSearched, searchParams]);

  // Handlers
  const handleSearch = () => {
    if (!dateRange.start || !dateRange.end) {
      return;
    }

    setIsSearched(true);
    setSearchParams({
      startDate: dateRange.start,
      endDate: dateRange.end,
    });
  };

  const handleViewDetail = (diagnosis: AiSkincareDiagnosis) => {
    void diagnosis;
  };

  const handleStartDiagnosis = () => {};

  return (
    <div className="flex min-h-screen bg-background justify-center">
      <div className="flex w-full max-w-[1200px]">
        <MyPageSidebar />
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-6 py-8">
            {/* Page Header */}
            <div className="pl-1">
              <h1 className="text-2xl font-bold text-foreground pb-3">AI 피부관리 내역</h1>
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
              filteredDiagnoses.length > 0 ? (
                <AiSkincareList diagnoses={filteredDiagnoses} onViewDetail={handleViewDetail} />
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
  );
}
