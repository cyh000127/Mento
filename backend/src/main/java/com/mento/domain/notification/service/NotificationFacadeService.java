package com.mento.domain.notification.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.mento.common.util.TimeUtils;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.event.NotificationEvent;
import com.mento.domain.notification.repository.SseEmitterRepository;
import com.mento.domain.notification.service.command.NotificationCommandService;
import com.mento.domain.notification.service.query.NotificationQueryService;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationFacadeService {

	private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;
	private static final Integer EXPIRE_DATE = 90;

	private final NotificationCommandService notificationCommandService;
	private final NotificationQueryService notificationQueryService;
	private final UserQueryService userQueryService;

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

			List<Notification> unreadNotifications = notificationQueryService
				.findActiveNotifications(userId, TimeUtils.nowAsLocalDateTime());

			if (!unreadNotifications.isEmpty()) {
				List<NotificationResDto> initialData = unreadNotifications.stream()
					.map(NotificationConverter::toNotificationResDto)
					.toList();

				emitter.send(SseEmitter.event()
					.name("initial-notifications")
					.data(initialData));
			}

		} catch (IOException e) {
			log.error("[Notification] 초기 알림 전송 실패 {userId: {}}", userId, e);
			sseEmitterRepository.deleteById(userId);
		}

		return emitter;
	}

	@Transactional
	public void sendNotification(final NotificationSendReqDto dto) {
		LocalDateTime expiredAt = TimeUtils.nowAsLocalDateTime().plusDays(EXPIRE_DATE);
		if (dto.expiredAt() != null) {
			expiredAt = dto.expiredAt();
		}
		User user = userQueryService.findById(dto.targetMemberId());
		Notification notification = NotificationConverter.toEntity(dto, user, expiredAt);
		Notification savedNotification = notificationCommandService.save(notification);

		eventPublisher.publishEvent(new NotificationEvent(this, savedNotification));
	}

	@Transactional(readOnly = true)
	public List<NotificationResDto> getNotifications(final Long userId) {
		return notificationQueryService.getNotifications(userId).stream()
			.map(NotificationConverter::toNotificationResDto)
			.toList();
	}

	@Transactional
	public void deleteNotification(final Long userId, final Long notificationId) {
		notificationCommandService.delete(notificationId, userId);
	}
}
