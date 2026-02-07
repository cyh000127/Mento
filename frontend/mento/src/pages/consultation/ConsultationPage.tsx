import { useState, useEffect, useRef } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import { CategorySelection } from "@/components/consultation/category-selection";
import { DateTimeSelection } from "@/components/consultation/date-time-selection";
import { Questionnaire } from "@/components/consultation/questionnaire";
import { SurveyComplete } from "@/components/consultation/survey-complete";
import { Payment } from "@/components/consultation/payment";
import { BookingComplete } from "@/components/consultation/booking-complete";
import { StepIndicator } from "@/components/consultation/step-indicator";
import type { ConsultationCategory } from "@/types/consultation";
import { createReservationDraft } from "@/api/reservationApi";
import { updateReservationSurvey } from "@/api/reservationSurveyApi";
import { requestPaymentReady } from "@/api/paymentApi";
import type { ReservationDraftSlotInfo } from "@/types/reservation";
import type { ReservationSurveyData } from "@/types/reservationSurvey";

interface BookingData {
  category: ConsultationCategory | null;
  date: Date | null;
  time: string;
  slotId: number | null;
  reservationId: number | null;
  draftSlotInfo: ReservationDraftSlotInfo | null;
  surveyInfo: ReservationSurveyData | null;
  paymentId: number | null;
}

interface StoredBookingData {
  category: ConsultationCategory | null;
  date: string | null;
  time: string;
  slotId: number | null;
  reservationId: number | null;
  draftSlotInfo: ReservationDraftSlotInfo | null;
  surveyInfo: ReservationSurveyData | null;
  paymentId: number | null;
}

const INITIAL_BOOKING_DATA: BookingData = {
  category: null,
  date: null,
  time: "",
  slotId: null,
  reservationId: null,
  draftSlotInfo: null,
  surveyInfo: null,
  paymentId: null,
};

const steps = [
  { id: 1, label: "분야 선택" },
  { id: 2, label: "일정 선택" },
  { id: 3, label: "설문 작성" },
  { id: 4, label: "결제" },
  { id: 5, label: "예약 완료" },
];

export default function ConsultationPage() {
  const navigate = useNavigate();
  const location = useLocation();
  const [currentStep, setCurrentStep] = useState(1);
  const [showSurveyComplete, setShowSurveyComplete] = useState(false);
  const [bookingData, setBookingData] = useState<BookingData>(INITIAL_BOOKING_DATA);
  const [answers, setAnswers] = useState<string[]>([]);
  const paymentLoadingRef = useRef(false);
  const [isPaymentDataReady, setIsPaymentDataReady] = useState(false);

  const resetSurveyState = () => {
    setAnswers([]);
    setShowSurveyComplete(false);
    localStorage.removeItem("consultationPreQuestions");
  };

  // 세션 스토리지 복원 함수
  const restoreFromSession = () => {
    const stored = sessionStorage.getItem("consultationBookingData");
    if (!stored) return;

    try {
      const parsed = JSON.parse(stored) as StoredBookingData;
      setBookingData({
        category: parsed.category ?? null,
        date: parsed.date ? new Date(parsed.date) : null,
        time: parsed.time ?? "",
        slotId: parsed.slotId ?? null,
        reservationId: parsed.reservationId ?? null,
        draftSlotInfo: parsed.draftSlotInfo ?? null,
        surveyInfo: parsed.surveyInfo ?? null,
        paymentId: parsed.paymentId ?? null,
      });
    } catch {
      // ignore invalid stored data
    }
  };

  useEffect(() => {
    const state = location.state as { step?: number; reservationId?: number } | null;

    // 1. 일반 진입 (state.step 없음) -> 초기화
    if (!state?.step) {
      setBookingData(INITIAL_BOOKING_DATA);
      resetSurveyState();
      sessionStorage.removeItem("consultationBookingData");
      setCurrentStep(1);
      return;
    }

    // 2. 예약 완료 페이지 진입
    if (state?.step === 5) {
      setCurrentStep(5);
      restoreFromSession();
      return;
    }

    // 3. 결제 페이지 진입
    if (state?.step === 4 && state?.reservationId) {
      const targetReservationId = state.reservationId;
      
      // 중복 로딩 방지
      if (paymentLoadingRef.current) {
        return;
      }
      
      // 결제 페이지로 바로 진입 시 예약 정보 로드
      const loadReservationForPayment = async () => {
        paymentLoadingRef.current = true;
        
        try {
          const { getReservationDetail } = await import("@/api/reservationApi");
          const detail = await getReservationDetail(targetReservationId);
          
          // 카테고리 매핑 (mentorTypeName -> ConsultationCategory)
          let category: ConsultationCategory | null = null;
          const mentorTypeName = detail.mentorTypeInfo?.mentorTypeName?.toLowerCase() || "";
          
          if (mentorTypeName.includes("스킨케어") || mentorTypeName.includes("skincare") || mentorTypeName.includes("스킨")) {
            category = "skincare";
          } else if (mentorTypeName.includes("뷰티") || mentorTypeName.includes("beauty")) {
            category = "beauty";
          } else if (mentorTypeName.includes("헤어") || mentorTypeName.includes("hair")) {
            category = "hair";
          }
          
          // 카테고리 없으면 기본값 설정
          if (!category) {
            console.warn(`[카테고리 매핑 실패] mentorTypeName: ${mentorTypeName}, 기본값 'general' (멘토 상담 상품) 사용`);
            category = "general";
          }
          
          setBookingData((prev) => ({
            ...prev,
            category,
            reservationId: detail.reservationId,
            date: detail.scheduledDate ? new Date(detail.scheduledDate) : null,
            time: detail.scheduledTime ?? "",
            draftSlotInfo: {
              timetableId: detail.timetableId,
              slotId: 0, // 실제 slotId는 예약에 포함되어 있지 않음
              scheduledTime: detail.scheduledTime ?? "",
              price: 35000, // 고정 가격 35000원
              maxCapacity: 1,
              currentCapacity: 1,
              availableCapacity: 0,
              status: "CONFIRMED",
            },
          }));
          setIsPaymentDataReady(true);
          setCurrentStep(4);
        } catch (error) {
          console.error("예약 정보 로드 실패:", error);
          navigate("/mypage/consultations");
        } finally {
          // 다음 프레임에서 플래그 해제 (상태 업데이트 완료 후)
          setTimeout(() => {
            paymentLoadingRef.current = false;
          }, 100);
        }
      };
      loadReservationForPayment();
    }
  }, [location.state, navigate]);

  const handleCategorySelect = (category: ConsultationCategory | null) => {
    resetSurveyState();
    setBookingData((prev) => ({ ...prev, category }));
  };

  // 260126 kjm - 사용하지 않고 있어서 빌드 에러 잡느라 지웁니다
  //              예약 붙일 때 함수 필요하면 살리기
  // const handleDateTimeSelect = (date: Date | null, time: string) => {
  //   setBookingData((prev) => ({ ...prev, date, time }))
  // }

  const handleNext = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
    setCurrentStep((prev) => Math.min(prev + 1, steps.length));
  };

  const handleNextFromDateTime = async () => {
    if (!bookingData.date || !bookingData.time || !bookingData.slotId) {
      return;
    }

    try {
      const data = await createReservationDraft({ slotId: bookingData.slotId });
      setBookingData((prev) => ({
        ...prev,
        reservationId: data.reservationId,
        draftSlotInfo: data.timetableSlotInfoDto,
      }));
      handleNext();
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      console.error(message);
    }
  };

  const handleBack = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
    setCurrentStep((prev) => {
      const nextStep = Math.max(prev - 1, 1);
      if (nextStep === 1) {
        resetSurveyState();
      }
      return nextStep;
    });
  };

  const handleQuestionnaireComplete = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
    setShowSurveyComplete(true);
  };

  const handleSurveySubmit = async (surveyData: string) => {
    if (!bookingData.reservationId) {
      return;
    }

    try {
      const data = await updateReservationSurvey(bookingData.reservationId, { surveyData });
      setBookingData((prev) => ({ ...prev, surveyInfo: data }));
      handleQuestionnaireComplete();
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      console.error(message);
    }
  };

  const handleGoToPayment = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
    setIsPaymentDataReady(true); // 일반 플로우에서는 이미 데이터가 준비되어 있음
    handleNext();
  };

  const handlePaymentReady = async () => {    
    const reservationId = bookingData.reservationId;
    const itemName = bookingData.category;
    const totalAmount = bookingData.draftSlotInfo?.price;

    if (!reservationId || !itemName || !totalAmount || totalAmount <= 0) {
      console.error("[결제 준비] 필수 데이터 누락", {
        reservationId,
        itemName,
        totalAmount,
        fullBookingData: bookingData,
      });
      return;
    }

    try {
      const data = await requestPaymentReady({
        reservationId,
        itemName,
        totalAmount,
      });
      setBookingData((prev) => ({ ...prev, paymentId: data.paymentId }));
      const bookingDataSnapshot: StoredBookingData = {
        category: bookingData.category,
        date: bookingData.date ? bookingData.date.toISOString() : null,
        time: bookingData.time,
        slotId: bookingData.slotId,
        reservationId: bookingData.reservationId,
        draftSlotInfo: bookingData.draftSlotInfo,
        surveyInfo: bookingData.surveyInfo,
        paymentId: data.paymentId,
      };
      sessionStorage.setItem("consultationBookingData", JSON.stringify(bookingDataSnapshot));
      localStorage.setItem("paymentId", String(data.paymentId));
      navigate("/consultation/payment-redirect", {
        state: { redirectUrl: data.redirectUrl },
      });
    } catch (error) {
      const message = error instanceof Error ? error.message : String(error);
      console.error("[결제 준비] 요청 실패:", message);
    }
  };

  const handleBackFromPayment = () => {
    window.scrollTo({ top: 0, behavior: "smooth" });
    setIsPaymentDataReady(false);
    setShowSurveyComplete(true);
    handleBack();
  };

  const handleAnswerChange = (index: number, answer: string) => {
    setAnswers((prev) => {
      const next = [...prev];
      next[index] = answer;
      return next;
    });
  };

  return (
    <div className="min-h-screen bg-background py-12">
      <div className="mx-auto max-w-[1000px] px-6">
        {/* Step Indicator */}
        <StepIndicator steps={steps} currentStep={currentStep} />

        {/* Step Content */}
        <div className="mt-12">
          {currentStep === 1 && <CategorySelection selectedCategory={bookingData.category} onSelect={handleCategorySelect} onNext={handleNext} canProceed={bookingData.category !== null} />}

          {currentStep === 2 && (
            <DateTimeSelection
              selectedDate={bookingData.date}
              selectedTime={bookingData.time}
              selectedCategory={bookingData.category}
              onDateSelect={(date) =>
                setBookingData((prev) => ({
                  ...prev,
                  date,
                  reservationId: null,
                  draftSlotInfo: null,
                  surveyInfo: null,
                  paymentId: null,
                }))
              }
              onTimeSelect={(time, slotId) =>
                setBookingData((prev) => ({
                  ...prev,
                  time,
                  slotId,
                  reservationId: null,
                  draftSlotInfo: null,
                  surveyInfo: null,
                  paymentId: null,
                }))
              }
              onNext={handleNextFromDateTime}
              onPrev={handleBack}
              canProceed={bookingData.date !== null && bookingData.time !== ""}
            />
          )}

          {currentStep === 3 && !showSurveyComplete && (
            <Questionnaire
              answers={answers}
              selectedCategory={bookingData.category}
              onAnswerChange={handleAnswerChange}
              onSubmitSurvey={handleSurveySubmit}
              onPrev={handleBack}
              canProceed={answers.every((a) => a && a.trim() !== "")}
            />
          )}

          {currentStep === 3 && showSurveyComplete && <SurveyComplete onGoToPayment={handleGoToPayment} />}

          {currentStep === 4 && isPaymentDataReady && <Payment bookingData={bookingData} onPrev={handleBackFromPayment} onPaymentReady={handlePaymentReady} />}
          {currentStep === 4 && !isPaymentDataReady && (
            <div className="flex items-center justify-center py-12">
              <div className="text-center">
                <div className="inline-block h-8 w-8 animate-spin rounded-full border-4 border-solid border-primary-500 border-r-transparent"></div>
                <p className="mt-4 text-text-secondary">결제 정보를 불러오는 중...</p>
              </div>
            </div>
          )}

          {currentStep === 5 && <BookingComplete bookingData={bookingData} />}
        </div>
      </div>
    </div>
  );
}
