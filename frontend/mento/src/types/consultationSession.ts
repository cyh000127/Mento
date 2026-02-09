export type ConsultationParticipantRole = "MENTOR" | "USER";

export interface ConsultationSessionData {
  timetableId: number;
  roomToken: string;
  roomName: string;
  livekitUrl: string;
  participantRole: ConsultationParticipantRole;
  enteredAt: string;
}

export interface ConsultationSessionResponse {
  success: boolean;
  data: ConsultationSessionData;
  error: string | null;
  timestamp: string;
}
