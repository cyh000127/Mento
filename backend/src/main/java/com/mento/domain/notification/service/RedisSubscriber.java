package com.mento.domain.notification.service;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.dto.message.NotificationMessage;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.repository.SseEmitterRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisSubscriber implements MessageListener {

	private final ObjectMapper objectMapper;
	private final SseEmitterRepository sseEmitterRepository;

	@Override
	public void onMessage(final @NonNull Message message, final byte[] pattern) {
		try {
			String body = new String(message.getBody());
			NotificationMessage notificationMessage = objectMapper.readValue(body, NotificationMessage.class);

			Long userId = notificationMessage.userId();
			NotificationResDto resDto = NotificationConverter.toNotificationResDto(notificationMessage);

			sseEmitterRepository.findById(userId).ifPresent(emitter -> {
				try {
					emitter.send(SseEmitter.event()
						.name("notification")
						.data(resDto));
					log.info("[onMessage] 알림 전송 성공 {userId: {}, notificationId: {}}", userId, resDto.notificationId());
				} catch (IOException _) {
					log.error("[onMessage] SSE 알림 전송 실패 {userId: {}}", userId);
					sseEmitterRepository.deleteById(userId);
				}
			});

		} catch (Exception e) {
			log.error("[onMessage] Redis 메시지 처리 중 오류 발생", e);
		}
	}
}
