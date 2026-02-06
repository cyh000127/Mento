import { useEffect, useMemo, useRef, useState } from "react";
import { ArrowLeft, Loader2 } from "lucide-react";
import { Button } from "@/components/ui/button";
import { MyPageSidebar } from "@/components/mypage/mypage-sidebar";
import { PeriodDateFilters } from "@/components/mypage/consultation-filters";
import { AiSkincareList } from "@/components/mypage/ai-skincare-list";
import { AiSkincareEmpty } from "@/components/mypage/ai-skincare-empty";
import { SkinAnalysisResult } from "@/components/ai-care/skin-analysis-result";
import type { AiSkincareDiagnosis, PeriodFilter, SkinAnalysisDetailData } from "@/types/ai-skincare";
import { getSkinAnalysisHistory, getSkinAnalysisDetail } from "@/api/skinAnalysisApi";

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

  // Detail view states
  const [selectedDiagnosis, setSelectedDiagnosis] = useState<AiSkincareDiagnosis | null>(null);
  const [detailData, setDetailData] = useState<SkinAnalysisDetailData | null>(null);
  const [isLoadingDetail, setIsLoadingDetail] = useState(false);
  const [detailError, setDetailError] = useState<string | null>(null);

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
    // Show all diagnoses by default when page loads
    if (!isSearched || !searchParams) {
      return diagnoses;
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

  const handleViewDetail = async (diagnosis: AiSkincareDiagnosis) => {
    setSelectedDiagnosis(diagnosis);
    setIsLoadingDetail(true);
    setDetailError(null);
    setDetailData(null);

    try {
      const data = await getSkinAnalysisDetail(diagnosis.id);
      setDetailData(data);
    } catch (error) {
      console.error("Failed to fetch detail:", error);
      setDetailError("상세 정보를 불러오는데 실패했습니다.");
    } finally {
      setIsLoadingDetail(false);
    }
  };

  const handleBackToList = () => {
    setSelectedDiagnosis(null);
    setDetailData(null);
    setDetailError(null);
  };

  const handleStartDiagnosis = () => {};

  return (
    <div className="flex min-h-screen bg-background justify-center">
      <div className="flex w-full max-w-[1200px]">
        <MyPageSidebar />
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-6 py-8">
            {selectedDiagnosis ? (
              // Detail View
              <div>
                <Button onClick={handleBackToList} variant="ghost" className="text-muted-foreground hover:text-foreground">
                  <ArrowLeft className="mr-2 h-4 w-4" />
                  목록으로
                </Button>

                {/* Loading State */}
                {isLoadingDetail && (
                  <div className="flex min-h-[60vh] flex-col items-center justify-center text-center">
                    <div className="mb-8 flex h-24 w-24 items-center justify-center rounded-full bg-primary-100">
                      <Loader2 className="h-12 w-12 animate-spin text-primary-500" />
                    </div>
                    <h2 className="mb-4 text-2xl font-bold text-text-primary md:text-3xl">데이터를 불러오고 있습니다</h2>
                    <p className="text-text-secondary">잠시만 기다려주세요.</p>
                  </div>
                )}

                {/* Error State */}
                {detailError && (
                  <div className="rounded-xl border border-red-200 bg-red-50 p-6 text-center">
                    <p className="text-red-600">{detailError}</p>
                  </div>
                )}

                {/* Detail Result */}
                {detailData && !isLoadingDetail && <SkinAnalysisResult analysisResult={detailData} showRetryButton={false} />}
              </div>
            ) : (
              // List View
              <>
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
                {filteredDiagnoses.length > 0 ? <AiSkincareList diagnoses={filteredDiagnoses} onViewDetail={handleViewDetail} /> : <AiSkincareEmpty onStartDiagnosis={handleStartDiagnosis} />}
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
