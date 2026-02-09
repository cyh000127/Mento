import { create } from "zustand";

interface BasicParticipantInfo {
  id: number;
  name: string;
}

interface MentorTypeInfo {
  id: number;
  name: string;
}

interface ConsultationParticipantState {
  userInfo: BasicParticipantInfo | null;
  mentorInfo: BasicParticipantInfo | null;
  mentorTypeInfo: MentorTypeInfo | null;
  setReservationInfo: (payload: {
    userInfo?: BasicParticipantInfo | null;
    mentorInfo?: BasicParticipantInfo | null;
    mentorTypeInfo?: MentorTypeInfo | null;
  }) => void;
  clearReservationInfo: () => void;
}

export const useConsultationParticipantStore = create<ConsultationParticipantState>((set) => ({
  userInfo: null,
  mentorInfo: null,
  mentorTypeInfo: null,
  setReservationInfo: (payload) =>
    set({
      userInfo: payload.userInfo ?? null,
      mentorInfo: payload.mentorInfo ?? null,
      mentorTypeInfo: payload.mentorTypeInfo ?? null,
    }),
  clearReservationInfo: () => set({ userInfo: null, mentorInfo: null, mentorTypeInfo: null }),
}));
