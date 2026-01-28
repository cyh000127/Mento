import { useState } from "react"
import { CategorySelection } from "@/components/consultation/category-selection"
import { DateTimeSelection } from "@/components/consultation/date-time-selection"
import { Questionnaire } from "@/components/consultation/questionnaire"
import { SurveyComplete } from "@/components/consultation/survey-complete"
import { Payment } from "@/components/consultation/payment"
import { BookingComplete } from "@/components/consultation/booking-complete"
import { StepIndicator } from "@/components/consultation/step-indicator"
import type { ConsultationCategory } from "@/types/consultation"

interface BookingData {
  category: ConsultationCategory | null
  date: Date | null
  time: string
}

const steps = [
  { id: 1, label: "분야 선택" },
  { id: 2, label: "일정 선택" },
  { id: 3, label: "설문 작성" },
  { id: 4, label: "결제" },
  { id: 5, label: "예약 완료" },
]

export default function ConsultationPage() {
  const [currentStep, setCurrentStep] = useState(1)
  const [showSurveyComplete, setShowSurveyComplete] = useState(false)
  const [bookingData, setBookingData] = useState<BookingData>({
    category: null,
    date: null,
    time: "",
  })

  const handleCategorySelect = (category: ConsultationCategory | null) => {
    setBookingData((prev) => ({ ...prev, category }))
  }

  // 260126 kjm - 사용하지 않고 있어서 빌드 에러 잡느라 지웁니다
  //              예약 붙일 때 함수 필요하면 살리기 
  // const handleDateTimeSelect = (date: Date | null, time: string) => {
  //   setBookingData((prev) => ({ ...prev, date, time }))
  // }

  const handleNext = () => {
    window.scrollTo({ top: 0, behavior: "smooth" })
    setCurrentStep((prev) => Math.min(prev + 1, steps.length))
  }

  const handleBack = () => {
    window.scrollTo({ top: 0, behavior: "smooth" })
    setCurrentStep((prev) => Math.max(prev - 1, 1))
  }

  const handleQuestionnaireComplete = () => {
    window.scrollTo({ top: 0, behavior: "smooth" })
    setShowSurveyComplete(true)
  }

  const handleSurveyCompleteNext = () => {
    window.scrollTo({ top: 0, behavior: "smooth" })
    setShowSurveyComplete(false)
    handleNext()
  }

  const handleBackFromPayment = () => {
    window.scrollTo({ top: 0, behavior: "smooth" })
    setShowSurveyComplete(true)
    handleBack()
  }

  const handlePaymentComplete = () => {
    window.scrollTo({ top: 0, behavior: "smooth" })
    handleNext() // Step 5 (예약 완료)로 이동
  }

  const [answers, setAnswers] = useState<string[]>([])
  const handleAnswerChange = (index: number, answer: string) => {
    setAnswers((prev) => {
      const next = [...prev]
      next[index] = answer
      return next
    })
  }
  

  return (
    <div className="min-h-screen bg-background py-12">
      <div className="mx-auto max-w-[1000px] px-6">
        {/* Step Indicator */}
        <StepIndicator steps={steps} currentStep={currentStep} />

        {/* Step Content */}
        <div className="mt-12">
          {currentStep === 1 && (
            <CategorySelection
              selectedCategory={bookingData.category}
              onSelect={handleCategorySelect}
              onNext={handleNext}
              canProceed={bookingData.category !== null}
            />
          )}

          {currentStep === 2 && (
            <DateTimeSelection
              selectedDate={bookingData.date}
              selectedTime={bookingData.time}
              selectedCategory={bookingData.category}
              onDateSelect={(date) =>
                setBookingData((prev) => ({ ...prev, date }))
              }
              onTimeSelect={(time) =>
                setBookingData((prev) => ({ ...prev, time }))
              }
              onNext={handleNext}
              onPrev={handleBack}
              canProceed={bookingData.date !== null && bookingData.time !== ""}
            />
          )}

          {currentStep === 3 && !showSurveyComplete && (
            <Questionnaire
              answers={answers}
              selectedCategory={bookingData.category}
              onAnswerChange={handleAnswerChange}
              onNext={handleQuestionnaireComplete}
              onPrev={handleBack}
              canProceed={answers.every((a) => a && a.trim() !== "")}
            />
          )}

          {currentStep === 3 && showSurveyComplete && (
            <SurveyComplete onNext={handleSurveyCompleteNext} />
          )}

          {currentStep === 4 && (
            <Payment
              bookingData={bookingData}
              onPrev={handleBackFromPayment}
              onPaymentComplete={handlePaymentComplete}
            />
          )}

          {currentStep === 5 && (
            <BookingComplete bookingData={bookingData} />
          )}
        </div>
      </div>
    </div>
  )
}
