import { api, API_BASE } from "./axios";
import type { NotificationResponse } from "@/types/notification";

// SSE 연결을 위한 URL 반환 (토큰 포함)
export const getNotificationEventSourceUrl = (token: string): string => {
    return `${API_BASE}/api/v1/notifications/subscribe?token=${encodeURIComponent(token)}`;
};

/**
 * 알림 목록 조회
 */
export const getNotifications = async (): Promise<NotificationResponse> => {
    const { data } = await api.get<NotificationResponse>("/notifications");
    return data;
};

/**
 * 알림 삭제
 */
export const deleteNotification = async (notificationId: number): Promise<void> => {
    await api.delete(`/notifications/${notificationId}`);
};
