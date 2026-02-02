export interface PaymentReadyRequest {
  reservationId: number;
  itemName: string;
  totalAmount: number;
}

export interface PaymentReadyData {
  paymentId: number;
  redirectUrl: string;
}

export interface PaymentReadyError {
  status: string;
  message: string;
  method: string;
  requestUri: string;
  errors: unknown[];
}

export interface PaymentReadyResponse {
  success: boolean;
  data: PaymentReadyData | null;
  error: PaymentReadyError | null;
  timestamp: string;
}
