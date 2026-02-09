import { create } from "zustand";
import type { ConsultationReportData } from "@/types/consultation";

interface ConsultationReportState {
  report: ConsultationReportData | null;
  setReport: (report: ConsultationReportData) => void;
  clearReport: () => void;
}

export const useConsultationReportStore = create<ConsultationReportState>((set) => ({
  report: null,
  setReport: (report) => set({ report }),
  clearReport: () => set({ report: null }),
}));
