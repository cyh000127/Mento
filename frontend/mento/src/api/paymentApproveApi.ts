import { api } from "./axios";
import type { PaymentApproveRequest, PaymentApproveResponse, PaymentApproveData } from "@/types/paymentApprove";

export async function approvePaymentReservation(payload: PaymentApproveRequest): Promise<PaymentApproveData> {
  const response = await api.post<PaymentApproveResponse>("/payments/approve/reservation", payload);
  if (!response.data.success || !response.data.data) {
    throw new Error(response.data.error?.message ?? "결제 승인에 실패했습니다.");
  }
  return response.data.data;
}
