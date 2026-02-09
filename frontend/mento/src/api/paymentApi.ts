import { api } from "./axios";
import type { PaymentReadyData, PaymentReadyRequest, PaymentReadyResponse } from "@/types/payment";

export async function requestPaymentReady(payload: PaymentReadyRequest): Promise<PaymentReadyData> {
  const response = await api.post<PaymentReadyResponse>("/payments/ready", payload);
  if (!response.data.success || !response.data.data) {
    throw new Error(response.data.error?.message ?? "결제 준비에 실패했습니다.");
  }
  return response.data.data;
}
