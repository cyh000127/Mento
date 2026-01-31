export type TimetableSlotStatus = "AVAILABLE" | "FULL" | "CLOSED"

export interface TimetableSlot {
  timetableId: number
  slotId: number
  scheduledTime: string
  price: number
  maxCapacity: number
  currentCapacity: number
  availableCapacity: number
  status: TimetableSlotStatus
}

export interface DailyTimetable {
  date: string
  slots: TimetableSlot[]
}

export interface MonthlyTimetablesData {
  startDate: string
  endDate: string
  totalDays: number
  mentorTypeId: number
  mentorTypeName: string
  dailyTimetables: DailyTimetable[]
}

export interface MonthlyTimetablesResponse {
  success: boolean
  data: MonthlyTimetablesData
}
