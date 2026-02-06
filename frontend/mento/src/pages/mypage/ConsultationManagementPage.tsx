import { useState, useMemo, useEffect, useRef } from "react";
import { useNavigate } from "react-router-dom";
import { ArrowLeft } from "lucide-react";
import { Button } from "@/components/ui/button";
import { MyPageSidebar } from "@/components/mypage/mypage-sidebar";
import { PeriodDateFilters } from "@/components/mypage/consultation-filters";
import { ConsultationCategoryFilter } from "@/components/mypage/consultation-category-filter";
import { ConsultationList } from "@/components/mypage/consultation-list";
import { ConsultationEmpty } from "@/components/mypage/consultation-empty";
import { ConsultationDetail } from "@/components/mypage/consultation-detail";
import { ReportDetail } from "@/components/mypage/report-detail";
import { getReservationDetail, getReservationList } from "@/api/reservationApi";
import { getConsultingReportDetail } from "@/api/consultationReportApi";
import { useAuthStore } from "@/stores/useAuthStore";
import { useConsultationReportStore } from "@/stores/useConsultationReportStore";
import { AlertModal } from "@/components/common/alert-modal";
import type { AlertModalType } from "@/components/common/alert-modal";
import type { Consultation, PeriodFilter, ConsultationStatus, PreConsultationQA } from "@/types/consultation";
import type { ReservationListItem, ReservationListParams } from "@/types/reservationList";
import type { ReservationDetailData } from "@/types/reservationDetail";

const pageSize = 10;

const parseScheduledDateTime = (scheduledDate: string) => {
  if (scheduledDate.includes("T")) {
    const [datePart, timePart] = scheduledDate.split("T");
    return { date: datePart, time: timePart.slice(0, 5) };
  }

  if (scheduledDate.includes(" ")) {
    const [datePart, timePart] = scheduledDate.split(" ");
    return { date: datePart, time: timePart.slice(0, 5) };
  }

  return { date: scheduledDate, time: "00:00" };
};

const mapReservationStatusToConsultationStatus = (status: string): ConsultationStatus => {
  const normalizedStatus = status.toLowerCase();

  if (normalizedStatus.includes("cancel")) {
    return "cancelled";
  }

  if (normalizedStatus.includes("complete")) {
    return "completed";
  }

  if (normalizedStatus.includes("in_progress") || normalizedStatus.includes("inprogress")) {
    return "pending";
  }

  if (normalizedStatus.includes("confirmed")) {
    return "scheduled";
  }

  return "scheduled";
};

const normalizeScheduledDateTime = (scheduledDate: string, scheduledTime?: string) => {
  if (scheduledTime) {
    const dateOnly = scheduledDate.includes("T") ? scheduledDate.split("T")[0] : scheduledDate.includes(" ") ? scheduledDate.split(" ")[0] : scheduledDate;

    return { date: dateOnly, time: scheduledTime };
  }

  return parseScheduledDateTime(scheduledDate);
};

const normalizeSurveyItems = (items?: { question: string; answer: string }[]): PreConsultationQA[] | undefined => {
  if (!items || items.length === 0) return undefined;

  const normalized = items
    .filter((item) => item && typeof item.question === "string")
    .map((item) => ({
      question: item.question,
      answer: typeof item.answer === "string" ? item.answer : "",
    }));

  return normalized.length > 0 ? normalized : undefined;
};

const parseSurveyDataToQA = (surveyData?: string): PreConsultationQA[] | undefined => {
  if (!surveyData) return undefined;

  try {
    const parsed = JSON.parse(surveyData) as { items?: { question: string; answer: string }[] } | { question: string; answer: string }[];

    const items = Array.isArray(parsed) ? parsed : parsed.items;
    return normalizeSurveyItems(items);
  } catch {
    return undefined;
  }
};

const mapReservationToConsultation = (reservation: ReservationListItem): Consultation => {
  const { date, time } = parseScheduledDateTime(reservation.scheduledDate);
  const scheduledTime = reservation.scheduledTime ?? time;

  return {
    id: reservation.reservationId.toString(),
    scheduledDate: date,
    scheduledTime,
    status: mapReservationStatusToConsultationStatus(reservation.status),
    mentorTypeName: reservation.mentorType.name,
    memo: reservation.mentorType.description,
    reservationId: reservation.reservationId,
    reportId: reservation.reportId,
  };
};

const mapReservationDetailToConsultation = (reservation: ReservationDetailData): Consultation => {
  const { date, time } = normalizeScheduledDateTime(reservation.scheduledDate, reservation.scheduledTime);
  const preConsultationQA = normalizeSurveyItems(reservation.surveyInfo?.surveys) ?? parseSurveyDataToQA(reservation.surveyData);

  const consultation: Consultation = {
    id: reservation.reservationId.toString(),
    scheduledDate: date,
    scheduledTime: time,
    status: mapReservationStatusToConsultationStatus(reservation.reservationStatus),
    preConsultationQA,
    surveyInfo: preConsultationQA ? { surveys: preConsultationQA } : undefined,
    reservationId: reservation.reservationId,
    reportId: reservation.reportId,
  };

  if (reservation.mentorInfo) {
    consultation.expertName = reservation.mentorInfo.mentorName;
  }

  if (reservation.mentorTypeInfo) {
    consultation.mentorTypeName = reservation.mentorTypeInfo.mentorTypeName;
    consultation.memo = reservation.mentorTypeInfo.mentorTypeDescription;
  }

  return consultation;
};

// 시연 끝나고 살리기기 kjm 2026-02-06
// const canEnterConsultationRoom = (consultation: Consultation) => {
//   const { scheduledDate, scheduledTime } = consultation;

//   if (!scheduledDate || !scheduledTime) return false;

//   // 상담 시작 시간
//   const startDateTime = new Date(`${scheduledDate}T${scheduledTime}:00`);
//   const now = new Date();

//   // 상담 시작 10분 전부터 입장 가능
//   const enterAvailableTime = new Date(startDateTime.getTime() - 10 * 60 * 1000);

//   return now >= enterAvailableTime;
// };

export default function ConsultationManagementPage() {
  const navigate = useNavigate();
  const { user, accessToken } = useAuthStore();
  const { setReport } = useConsultationReportStore();
  const lastRequestKeyRef = useRef<string | null>(null);
  const isFetchingRef = useRef(false);
  const detailFetchingIdRef = useRef<number | null>(null);
  const detailCacheRef = useRef<Map<number, Consultation>>(new Map());
  // View state
  const [selectedConsultation, setSelectedConsultation] = useState<Consultation | null>(null);
  const [selectedReportConsultation, setSelectedReportConsultation] = useState<Consultation | null>(null);

  // Filter states
  const [selectedPeriod, setSelectedPeriod] = useState<PeriodFilter>("1month");
  const [startYear, setStartYear] = useState("");
  const [startMonth, setStartMonth] = useState("");
  const [startDay, setStartDay] = useState("");
  const [endYear, setEndYear] = useState("");
  const [endMonth, setEndMonth] = useState("");
  const [endDay, setEndDay] = useState("");
  const [selectedCategory, setSelectedCategory] = useState<string | "all">("all");
  const [isSearched, setIsSearched] = useState(false);
  const [searchParams, setSearchParams] = useState<{
    startDate: string;
    endDate: string;
  } | null>(null);

  // Data state
  const [consultations, setConsultations] = useState<Consultation[]>([]);
  const [pagination, setPagination] = useState({
    hasNext: false,
    totalPages: 0,
    totalElements: 0,
    page: 0,
    size: pageSize,
    isFirst: true,
    isLast: true,
  });
  const [currentPage, setCurrentPage] = useState(0);

  // Alert state
  const [alertState, setAlertState] = useState({
    open: false,
    title: "알림",
    message: "",
    type: "info" as AlertModalType,
    confirmText: "확인",
  });

  const showAlert = (options: { title?: string; message: string; type?: AlertModalType; confirmText?: string }) => {
    setAlertState({
      open: true,
      title: options.title ?? "알림",
      message: options.message,
      type: options.type ?? "info",
      confirmText: options.confirmText ?? "확인",
    });
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

  const mentorTypeOptions = useMemo(() => {
    return ["스킨케어", "뷰티", "헤어"];
  }, []);

  useEffect(() => {
    if (selectedCategory === "all") return;
    if (!mentorTypeOptions.includes(selectedCategory)) {
      setSelectedCategory("all");
    }
  }, [mentorTypeOptions, selectedCategory]);

  useEffect(() => {
    const fetchReservations = async () => {
      try {
        if (!accessToken && !user) {
          return;
        }

        const currentUser = user;
        if (!currentUser) {
          return;
        }

        const params: ReservationListParams = searchParams
          ? {
              startDate: searchParams.startDate,
              endDate: searchParams.endDate,
              page: currentPage,
              size: pageSize,
            }
          : {
              startDate: "",
              endDate: "",
              page: currentPage,
              size: pageSize,
            };

        const requestKey = JSON.stringify({
          startDate: params.startDate,
          endDate: params.endDate,
          page: params.page ?? null,
          size: params.size ?? null,
          userId: currentUser?.id ?? null,
        });

        if (requestKey === lastRequestKeyRef.current || isFetchingRef.current) {
          return;
        }

        isFetchingRef.current = true;
        lastRequestKeyRef.current = requestKey;

        const data = await getReservationList(params);

        setConsultations(data.content.map(mapReservationToConsultation));
        setPagination({
          hasNext: data.hasNext,
          totalPages: data.totalPages,
          totalElements: data.totalElements,
          page: data.page,
          size: data.size,
          isFirst: data.isFirst,
          isLast: data.isLast,
        });
        setIsSearched(true);

        if (data.page !== currentPage) {
          setCurrentPage(data.page);
        }
      } catch (error) {
        const message = error instanceof Error ? error.message : String(error);
        console.error(message);
      } finally {
        isFetchingRef.current = false;
      }
    };

    fetchReservations();
  }, [currentPage, searchParams, accessToken, user]);

  useEffect(() => {
    if (currentPage > pagination.page) {
      setCurrentPage(pagination.page);
    }
  }, [currentPage, pagination.page]);

  // Sort consultations by date (most recent first)
  const sortedConsultations = useMemo(() => {
    if (!isSearched) return [];

    const filtered = selectedCategory === "all" ? consultations : consultations.filter((consultation) => consultation.mentorTypeName === selectedCategory);

    return [...filtered].sort((a, b) => {
      return new Date(b.scheduledDate).getTime() - new Date(a.scheduledDate).getTime();
    });
  }, [consultations, isSearched, selectedCategory]);

  // Handlers
  const handleSearch = () => {
    if (!dateRange.start || !dateRange.end) {
      return;
    }

    setIsSearched(true);
    setCurrentPage(0);
    setSearchParams({
      startDate: dateRange.start,
      endDate: dateRange.end,
    });
  };

  const handleViewDetail = async (consultation: Consultation) => {
    const reservationId = Number(consultation.id);
    if (!Number.isFinite(reservationId)) {
      return;
    }

    const cached = detailCacheRef.current.get(reservationId);
    if (cached) {
      setSelectedConsultation(cached);
      // 캐시된 데이터로 목록도 업데이트
      setConsultations((prev) => prev.map((item) => (item.id === consultation.id ? { ...item, status: cached.status } : item)));
      return;
    }

    if (detailFetchingIdRef.current === reservationId) {
      return;
    }

    detailFetchingIdRef.current = reservationId;

    try {
      const detail = await getReservationDetail(reservationId);
      const mapped = mapReservationDetailToConsultation(detail);
      detailCacheRef.current.set(reservationId, mapped);
      setSelectedConsultation(mapped);

      // 목록의 해당 항목도 업데이트 (status 동기화)
      setConsultations((prev) => prev.map((item) => (item.id === consultation.id ? { ...item, status: mapped.status } : item)));
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      console.error(message);
    } finally {
      detailFetchingIdRef.current = null;
    }
  };

  const handleBackToList = () => {
    setSelectedConsultation(null);
  };

  const handleEnterRoom = (reservationId: number) => {
    const encodedId = btoa(reservationId.toString());
    navigate(`/consultation-room/${encodedId}`);
  };
  // 시연 끝나고 살리기 kjm 2026-02-06
  // const handleEnterRoom = (consultation: Consultation) => {
  //   if (!canEnterConsultationRoom(consultation)) {
  //     showAlert({
  //       title: "입장 불가",
  //       message: "상담 시작 10분 전부터 상담방에 입장할 수 있습니다.",
  //       type: "warning",
  //     });
  //     return;
  //   }
  
  //   if (!consultation.reservationId) return;
  
  //   const encodedId = btoa(consultation.reservationId.toString());
  //   navigate(`/consultation-room/${encodedId}`);
  // };

  const handleBookConsultation = () => {
    // Navigate to consultation booking page
    console.log("Navigate to booking page");
  };

  const handleGoToPayment = (consultation: Consultation) => {
    if (!consultation.reservationId) {
      console.error("예약 ID가 없습니다");
      return;
    }
    // 결제 페이지로 이동 - 예약 ID와 함께 상담 예약 페이지로 이동
    navigate("/consultation", {
      state: {
        reservationId: consultation.reservationId,
        step: 4, // 결제 단계
      },
    });
  };

  const handleViewReport = async (consultation: Consultation) => {
    if (!consultation.reportId) {
      showAlert({
        title: "리포트 생성 중",
        message: "아직 생성된 상담 리포트가 없습니다.",
        type: "warning",
      });
      return;
    }

    try {
      const report = await getConsultingReportDetail(consultation.reportId);
      setReport(report);
      setSelectedReportConsultation(consultation);
    } catch (error) {
      console.error("Failed to fetch report:", error);
      showAlert({
        title: "상담 보고서 조회 실패",
        message: "상담 보고서를 불러오지 못했습니다.",
        type: "error",
      });
    }
  };

  const handleBackFromReport = () => {
    setSelectedReportConsultation(null);
  };

  const resetViewState = () => {
    setSelectedConsultation(null);
    setSelectedReportConsultation(null);
  };

  // Show report detail view if report consultation is selected
  if (selectedReportConsultation) {
    return (
      <div className="flex min-h-screen bg-background justify-center">
        <div className="flex w-full max-w-[1200px]">
          <MyPageSidebar onNavigate={resetViewState} />
          <div className="flex-1">
            <div className="bg-background py-8">
              <div className="mx-auto px-6">
                <div className="space-y-6">
                  {/* Header with Back Button */}
                  <div className="flex items-center justify-between border-b border-border pb-3">
                    <h1 className="text-2xl font-bold text-foreground">AI 리포트</h1>
                    <Button onClick={handleBackFromReport} variant="ghost" className="text-muted-foreground hover:text-foreground">
                      <ArrowLeft className="mr-2 h-4 w-4" />
                      목록으로
                    </Button>
                  </div>
                  <ReportDetail />
                </div>
              </div>
            </div>
          </div>
        </div>
        <AlertModal
          open={alertState.open}
          onOpenChange={(open) => setAlertState((prev) => ({ ...prev, open }))}
          title={alertState.title}
          message={alertState.message}
          type={alertState.type}
          confirmText={alertState.confirmText}
        />
      </div>
    );
  }

  // Show detail view if consultation is selected
  if (selectedConsultation) {
    return (
      <div className="flex min-h-screen bg-background justify-center">
        <div className="flex w-full max-w-[1200px]">
          <MyPageSidebar onNavigate={resetViewState} />
          <div className="flex-1">
            <ConsultationDetail consultation={selectedConsultation} onBack={handleBackToList} onGoToPayment={handleGoToPayment} />
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="flex min-h-screen bg-background justify-center">
      <div className="flex w-full max-w-[1200px]">
        <MyPageSidebar onNavigate={resetViewState} />
        <div className="flex-1">
          <div className="mx-auto max-w-7xl px-6 py-8">
            {/* Page Header */}
            <div className="pl-1">
              <h1 className="text-2xl font-bold text-foreground pb-3">상담</h1>
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

              <div className="mt-6 pt-6 border-t border-border">
                <ConsultationCategoryFilter options={mentorTypeOptions} selectedCategory={selectedCategory} onCategoryChange={setSelectedCategory} />
              </div>
            </div>

            {/* Consultation List or Empty State */}
            {isSearched ? (
              sortedConsultations.length > 0 ? (
                <ConsultationList consultations={sortedConsultations} onViewDetail={handleViewDetail} onEnterRoom={handleEnterRoom} onGoToPayment={handleGoToPayment} onViewReport={handleViewReport} />
              ) : (
                <ConsultationEmpty onBookConsultation={handleBookConsultation} />
              )
            ) : (
              <ConsultationEmpty onBookConsultation={handleBookConsultation} />
            )}
          </div>
        </div>
      </div>
      <AlertModal
        open={alertState.open}
        onOpenChange={(open) => setAlertState((prev) => ({ ...prev, open }))}
        title={alertState.title}
        message={alertState.message}
        type={alertState.type}
        confirmText={alertState.confirmText}
      />
    </div>
  );
}
