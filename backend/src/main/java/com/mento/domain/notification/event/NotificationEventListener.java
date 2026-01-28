package com.mento.domain.notification.event;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.domain.notification.entity.Notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleNotificationEvent(NotificationEvent event) {
		Notification notification = event.getNotification();
		try {
			String message = objectMapper.writeValueAsString(notification);
			redisTemplate.convertAndSend("notificationTopic", message);

			log.info("[NotificationEventListener] 알림 이벤트 발행 성공 {notificationId: {}, userId: {}}",
				notification.getId(),
				notification.getUserId());

		} catch (JsonProcessingException e) {
			log.error("[NotificationEventListener] 알림 이벤트 발행 실패 {notificationId: {}}",
				notification.getId(), e);
		}
	}
}