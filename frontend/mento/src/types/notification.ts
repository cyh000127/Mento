export type NotificationType =
    | "RESERVATION_REMINDER"
    | "RESERVATION_CONFIRMED"
    | "CONSULTING_STARTED"
    | "RESERVATION_CANCELLED"
    | "REPORT_READY"
    | "INVENTORY_EXPIRY";

export interface NotificationResDto {
    notificationId: number;
    type: NotificationType;
    content: string;
    createdAt: string;
}

export interface NotificationResponse {
    success: boolean;
    data: NotificationResDto[] | null;
    error: {
        status: string;
        message: string;
        method: string;
        requestUri: string;
        errors: string[];
    } | null;
    timestamp: string;
}
