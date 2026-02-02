export interface PaymentApproveRequest {
  paymentId: number;
  pgToken: string;
}

export interface PaymentApproveUserInfo {
  userId: number;
  userName: string;
}

export interface PaymentApproveMentorInfo {
  mentorId: number;
  mentorName: string;
}

export interface PaymentApproveMentorTypeInfo {
  mentorTypeId: number;
  mentorTypeName: string;
  mentorTypeDescription: string;
}

export interface PaymentApproveData {
  reservationId: number;
  userInfo: PaymentApproveUserInfo;
  mentorInfo: PaymentApproveMentorInfo;
  mentorTypeInfo: PaymentApproveMentorTypeInfo;
  timetableId: number;
  scheduledDate: string;
  scheduledTime: string;
  reservationStatus: "CONFIRMED" | string;
  createdAt: string;
  updatedAt: string;
}

export interface PaymentApproveError {
  status: string;
  message: string;
  method: string;
  requestUri: string;
  errors: unknown[];
}

export interface PaymentApproveResponse {
  success: boolean;
  data: PaymentApproveData | null;
  error: PaymentApproveError | null;
  timestamp: string;
}
