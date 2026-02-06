import { useState, useMemo, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { MyPageSidebar } from "@/components/mypage/mypage-sidebar";
import { PeriodDateFilters } from "@/components/mypage/consultation-filters";
import { InventoryHistoryList } from "@/components/mypage/inventory-history-list";
import { InventoryHistoryEmpty } from "@/components/mypage/inventory-history-empty";
import { getInventoryHistories } from "@/api/inventoryApi";
import type { InventoryHistoryItem } from "@/types/inventory";
import type { PeriodFilter } from "@/types/consultation";
import { ChevronLeft, ChevronRight } from "lucide-react";

export default function InventoryHistoryPage() {
  const navigate = useNavigate();

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
  const [histories, setHistories] = useState<InventoryHistoryItem[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Pagination state
  const [currentPage, setCurrentPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [hasNext, setHasNext] = useState(false);
  const pageSize = 20;

  const formatDateParts = (date: Date) => ({
    year: date.getFullYear().toString(),
    month: (date.getMonth() + 1).toString(),
    day: date.getDate().toString(),
  });

  const formatDateString = (date: Date) => {
    const { year, month, day } = formatDateParts(date);
    return `${year}-${month.padStart(2, "0")}-${day.padStart(2, "0")}`;
  };

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

  useEffect(() => {
    const today = new Date();
    const start = new Date();
    start.setMonth(today.getMonth() - 1);

    const startParts = formatDateParts(start);
    const endParts = formatDateParts(today);

    setStartYear(startParts.year);
    setStartMonth(startParts.month);
    setStartDay(startParts.day);
    setEndYear(endParts.year);
    setEndMonth(endParts.month);
    setEndDay(endParts.day);

    const nextParams = {
      startDate: formatDateString(start),
      endDate: formatDateString(today),
    };
    setIsSearched(true);
    setSearchParams(nextParams);
    setCurrentPage(0);
    fetchHistories(0, nextParams);
  }, []);

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

  // Fetch histories
  const fetchHistories = async (
    page: number = 0,
    params?: { startDate: string; endDate: string }
  ) => {
    const effectiveParams = params ?? searchParams;
    if (!effectiveParams) return;

    setIsLoading(true);
    setError(null);

    try {
      const response = await getInventoryHistories({
        page,
        size: pageSize,
        startDate: effectiveParams.startDate,
        endDate: effectiveParams.endDate,
      });

      setHistories(response.data.content);
      setCurrentPage(response.data.page);
      setTotalPages(response.data.totalPages);
      setHasNext(response.data.hasNext);
    } catch (err: any) {
      console.error("히스토리 조회 실패:", err);
      const status = err.response?.status;
      const errorCode = err.response?.data?.code;

      if (status === 400 && errorCode === "INVALID_DATE_RANGE") {
        setError(err.response?.data?.message || "유효하지 않은 날짜 범위입니다.");
      } else if (status === 401) {
        // 기존 인증 에러 핸들링 재사용
        setError("로그인이 필요합니다.");
      } else {
        setError("히스토리를 불러오는 중 오류가 발생했습니다.");
      }
      setHistories([]);
    } finally {
      setIsLoading(false);
    }
  };

  // Handlers
  const handleSearch = () => {
    if (!dateRange.start || !dateRange.end) {
      return;
    }

    setIsSearched(true);
    const nextParams = {
      startDate: dateRange.start,
      endDate: dateRange.end,
    };
    setSearchParams(nextParams);
    setCurrentPage(0);
    fetchHistories(0, nextParams);
  };

  const handlePreviousPage = () => {
    if (currentPage > 0) {
      const newPage = currentPage - 1;
      setCurrentPage(newPage);
      fetchHistories(newPage);
    }
  };

  const handleNextPage = () => {
    if (hasNext) {
      const newPage = currentPage + 1;
      setCurrentPage(newPage);
      fetchHistories(newPage);
    }
  };

  const handleGoToInventory = () => {
    navigate("/inventory");
  };

  return (
    <div className="flex min-h-screen bg-background justify-center">
      <div className="flex w-full max-w-[1200px]">
        <MyPageSidebar />
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-6 py-8">
            {/* Page Header */}
            <div className="pl-1">
              <h1 className="text-2xl font-bold text-foreground pb-3">인벤토리 내역</h1>
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

            {/* Error Message */}
            {error && (
              <div className="mb-6 rounded-xl border border-red-500/20 bg-red-500/10 p-4">
                <p className="text-sm text-red-500">{error}</p>
              </div>
            )}

            {/* Loading State */}
            {isLoading && (
              <div className="rounded-xl border border-border bg-card p-12 shadow-sm">
                <div className="flex items-center justify-center">
                  <p className="text-muted-foreground">로딩 중...</p>
                </div>
              </div>
            )}

            {/* History List or Empty State */}
            {!isLoading && isSearched && !error && (
              <>
                {histories.length > 0 ? (
                  <>
                    <InventoryHistoryList histories={histories} />

                    {/* Pagination */}
                    {totalPages > 1 && (
                      <div className="mt-6 flex items-center justify-center gap-2">
                        <button
                          onClick={handlePreviousPage}
                          disabled={currentPage === 0}
                          className="rounded-lg border border-border bg-card px-4 py-2 text-sm font-medium text-foreground hover:bg-muted transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          <ChevronLeft className="h-4 w-4" />
                        </button>
                        <span className="text-sm text-muted-foreground">
                          {currentPage + 1} / {totalPages}
                        </span>
                        <button
                          onClick={handleNextPage}
                          disabled={!hasNext}
                          className="rounded-lg border border-border bg-card px-4 py-2 text-sm font-medium text-foreground hover:bg-muted transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
                        >
                          <ChevronRight className="h-4 w-4" />
                        </button>
                      </div>
                    )}
                  </>
                ) : (
                  <InventoryHistoryEmpty onGoToInventory={handleGoToInventory} />
                )}
              </>
            )}

            {/* Initial State (before search) */}
            {!isLoading && !isSearched && (
              <div className="rounded-xl border border-border bg-card p-12 shadow-sm">
                <div className="flex items-center justify-center">
                  <p className="text-muted-foreground">조회 기간을 선택해주세요.</p>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
