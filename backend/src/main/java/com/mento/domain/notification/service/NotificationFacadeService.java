package com.mento.domain.notification.service;

import java.io.IOException;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.event.NotificationEvent;
import com.mento.domain.notification.repository.SseEmitterRepository;
import com.mento.domain.notification.service.command.NotificationCommandService;
import com.mento.domain.notification.service.query.NotificationQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationFacadeService {

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

	private final NotificationCommandService notificationCommandService;
	private final NotificationQueryService notificationQueryService;
	private final SseEmitterRepository sseEmitterRepository;
	private final ApplicationEventPublisher eventPublisher;


	public SseEmitter subscribe(final Long userId) {
		SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT);
		sseEmitterRepository.save(userId, emitter);


		emitter.onTimeout(() -> {
			log.info("[Notification] Emitter 타임아웃 발생 {userId: {}}", userId);
			sseEmitterRepository.deleteById(userId);
		});


		emitter.onCompletion(() -> {
			log.info("[Notification] Emitter 완료 {userId: {}}", userId);
			sseEmitterRepository.deleteById(userId);
		});


		try {
			emitter.send(SseEmitter.event()
				.name("connect")
				.data("connected!"));
		} catch (IOException e) {
			log.error("[Notification] 더미 이벤트 전송 실패 {userId: {}}", userId, e);
			sseEmitterRepository.deleteById(userId);
		}

		return emitter;
	}



	@Transactional
	public Notification sendNotification(final NotificationSendReqDto dto) {

		Notification notification = notificationCommandService.send(dto);
		

		eventPublisher.publishEvent(new NotificationEvent(this, notification));
		return notification;
	}

	@Transactional(readOnly = true)
	public Slice<NotificationResDto> getNotifications(final Long userId, final Pageable pageable) {
		return notificationQueryService.getNotifications(userId, pageable);
	}
}
