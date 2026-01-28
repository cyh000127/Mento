package com.mento.domain.notification.service;

import java.io.IOException;

import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;
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
	public void onMessage(final Message message, final byte[] pattern) {
		try {

			String body = new String(message.getBody());
			Notification notification = objectMapper.readValue(body, Notification.class);

			Long userId = notification.getUserId();
			NotificationResDto resDto = NotificationConverter.toNotificationResDto(notification);


			sseEmitterRepository.findById(userId).ifPresent(emitter -> {
				try {
					emitter.send(SseEmitter.event()
						.name("notification")
						.data(resDto));
					log.info("[onMessage] 알림 전송 성공 {userId: {}, notificationId: {}}", userId, resDto.id());
				} catch (IOException e) {
					log.error("[onMessage] SSE 알림 전송 실패 {userId: {}}", userId, e);
					sseEmitterRepository.deleteById(userId);
				}
			});

		} catch (Exception e) {
			log.error("[onMessage] Redis 메시지 처리 중 오류 발생", e);
		}
	}
}
