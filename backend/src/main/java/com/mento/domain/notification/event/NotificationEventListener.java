package com.mento.domain.notification.event;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.dto.message.NotificationMessage;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.repository.SseEmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	private final SseEmitterRepository sseEmitterRepository;

	@Async
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void handleNotificationEvent(NotificationEvent event) {
		Notification notification = event.getNotification();
		Long userId = notification.getUserId();

		if (sseEmitterRepository.findById(userId).isPresent()) {
			sendToLocalEmitter(userId, notification);
			return;
		}

		try {
			NotificationMessage message = NotificationConverter.toMessage(notification);
			String messageString = objectMapper.writeValueAsString(message);
			redisTemplate.convertAndSend("notificationTopic", messageString);

			log.info("[NotificationEventListener] 알림 이벤트 Redis 발행 성공 {notificationId: {}, userId: {}}",
				message.notificationId(),
				message.userId());

		} catch (JsonProcessingException e) {
			log.error("[NotificationEventListener] 알림 이벤트 Redis 발행 실패 {notificationId: {}}",
				notification.getId(), e);
		}
	}

	private void sendToLocalEmitter(Long userId, Notification notification) {
		sseEmitterRepository.findById(userId).ifPresent(emitter -> {
			try {
				NotificationResDto resDto = NotificationConverter.toNotificationResDto(notification);
				emitter.send(SseEmitter.event()
					.name("notification")
					.data(resDto));
				log.info("[NotificationEventListener] 로컬 SSE 전송 성공 {userId: {}, notificationId: {}}",
					userId, notification.getId());
			} catch (Exception e) {
				log.error("[NotificationEventListener] 로컬 SSE 전송 실패 {userId: {}}", userId, e);
				sseEmitterRepository.deleteById(userId);
			}
		});
	}
}