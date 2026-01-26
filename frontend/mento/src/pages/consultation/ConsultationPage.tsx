import { useState } from "react"
import { CategorySelection } from "@/components/consultation/category-selection"
import { DateTimeSelection } from "@/components/consultation/date-time-selection"
import { Questionnaire } from "@/components/consultation/questionnaire"
import { BookingComplete } from "@/components/consultation/booking-complete"
import { StepIndicator } from "@/components/consultation/step-indicator"

type ConsultationCategory = "skincare" | "beauty" | "hair" | null

interface BookingData {
  category: ConsultationCategory
  date: Date | null
  time: string
}

const steps = [
  { id: 1, label: "분야 선택" },
  { id: 2, label: "일정 선택" },
  { id: 3, label: "설문 작성" },
  { id: 4, label: "예약 완료" },
]

export default function ConsultationPage() {
  const [currentStep, setCurrentStep] = useState(1)
  const [bookingData, setBookingData] = useState<BookingData>({
    category: null,
    date: null,
    time: "",
  })

  const handleCategorySelect = (category: ConsultationCategory) => {
    setBookingData((prev) => ({ ...prev, category }))
  }

  const handleDateTimeSelect = (date: Date | null, time: string) => {
    setBookingData((prev) => ({ ...prev, date, time }))
  }

  const handleNext = () => {
    setCurrentStep((prev) => Math.min(prev + 1, steps.length))
  }

  const handleBack = () => {
    setCurrentStep((prev) => Math.max(prev - 1, 1))
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

          {currentStep === 3 && (
            <Questionnaire
              answers={answers}
              selectedCategory={bookingData.category}
              onAnswerChange={handleAnswerChange}
              onNext={handleNext}
              onPrev={handleBack}
              canProceed={answers.every((a) => a && a.trim() !== "")}
            />
          )}

          {currentStep === 4 && (
            <BookingComplete bookingData={bookingData} />
          )}
        </div>
      </div>
    </div>
  )
}
