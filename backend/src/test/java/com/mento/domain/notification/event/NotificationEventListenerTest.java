package com.mento.domain.notification.event;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.io.IOException;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.repository.SseEmitterRepository;
import com.mento.domain.user.entity.User;

class NotificationEventListenerTest {

	private final StringRedisTemplate redisTemplate = mock(StringRedisTemplate.class);
	private final SseEmitterRepository sseEmitterRepository = mock(SseEmitterRepository.class);
	private final NotificationEventListener notificationEventListener = new NotificationEventListener(
		redisTemplate,
		new ObjectMapper(),
		sseEmitterRepository
	);

	@Test
	@DisplayName("로컬 SSE 연결이 여러 개 있으면 모든 연결에 알림을 전송한다")
	void handleNotificationEvent_SendToAllLocalEmitters() throws Exception {
		// given
		Long userId = 1L;
		SseEmitter firstEmitter = mock(SseEmitter.class);
		SseEmitter secondEmitter = mock(SseEmitter.class);
		Notification notification = createNotification(userId);

		given(sseEmitterRepository.findAllByUserId(userId))
			.willReturn(Map.of("first", firstEmitter, "second", secondEmitter));

		// when
		notificationEventListener.handleNotificationEvent(new NotificationEvent(this, notification));

		// then
		verify(firstEmitter).send(any(SseEmitter.SseEventBuilder.class));
		verify(secondEmitter).send(any(SseEmitter.SseEventBuilder.class));
		verify(redisTemplate, never()).convertAndSend(anyString(), anyString());
	}

	@Test
	@DisplayName("로컬 SSE 전송 실패 시 실패한 연결만 제거한다")
	void handleNotificationEvent_DeleteOnlyFailedEmitter() throws Exception {
		// given
		Long userId = 1L;
		SseEmitter failedEmitter = mock(SseEmitter.class);
		SseEmitter activeEmitter = mock(SseEmitter.class);
		Notification notification = createNotification(userId);

		given(sseEmitterRepository.findAllByUserId(userId))
			.willReturn(Map.of("failed", failedEmitter, "active", activeEmitter));
		willThrow(new IOException("closed"))
			.given(failedEmitter)
			.send(any(SseEmitter.SseEventBuilder.class));

		// when
		notificationEventListener.handleNotificationEvent(new NotificationEvent(this, notification));

		// then
		verify(activeEmitter).send(any(SseEmitter.SseEventBuilder.class));
		verify(sseEmitterRepository).deleteById(userId, "failed");
		verify(sseEmitterRepository, never()).deleteById(userId, "active");
	}

	private Notification createNotification(final Long userId) {
		User user = User.builder()
			.id(userId)
			.build();

		return Notification.builder()
			.id(1L)
			.user(user)
			.type(NotificationType.REPORT_READY)
			.content("report")
			.build();
	}
}
