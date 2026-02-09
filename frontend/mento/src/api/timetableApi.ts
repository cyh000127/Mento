import { api } from "./axios"
import type { MonthlyTimetablesData, MonthlyTimetablesResponse } from "@/types/timetable"

export async function getMonthlyTimetablesByMentorType(
  typeId: number
): Promise<MonthlyTimetablesData> {
  const response = await api.get<MonthlyTimetablesResponse>("/timetables", {
    params: { typeId },
  })

  return response.data.data
}
