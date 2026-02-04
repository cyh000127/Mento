package com.mento.domain.notification.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.dto.response.NotificationResDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.event.NotificationEvent;
import com.mento.domain.notification.repository.SseEmitterRepository;
import com.mento.domain.notification.service.command.NotificationCommandService;
import com.mento.domain.notification.service.query.NotificationQueryService;

@ExtendWith(MockitoExtension.class)
class NotificationFacadeServiceTest {

	@InjectMocks
	private NotificationFacadeService notificationFacadeService;

	@Mock
	private NotificationCommandService notificationCommandService;

	@Mock
	private NotificationQueryService notificationQueryService;



	@Mock
	private SseEmitterRepository sseEmitterRepository;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Test
	@DisplayName("SSE 구독을 성공하고 미확인 알림을 전송한다")
	void subscribe_Success() {
		// given
		Long userId = 1L;

		Notification notification = Notification.builder()
			.id(1L)
			.userId(userId)
			.type(NotificationType.RESERVATION_REMINDER)
			.content("60")
			.build();

		List<Notification> unreadNotifications = List.of(notification);

		given(notificationQueryService.findActiveNotifications(eq(userId), any(LocalDateTime.class)))
			.willReturn(unreadNotifications);

		// when
		SseEmitter result = notificationFacadeService.subscribe(userId);

		// then
		assertThat(result).isNotNull();
		verify(sseEmitterRepository).save(eq(userId), any(SseEmitter.class));
	}

	@Test
	@DisplayName("알림을 발송하고 이벤트를 발행한다")
	void sendNotification_Success() {
		// given
		NotificationSendReqDto reqDto = new NotificationSendReqDto(
			1L, NotificationType.RESERVATION_REMINDER, "60", null
		);

		Notification notification = Notification.builder()
			.id(1L)
			.userId(1L)
			.type(NotificationType.RESERVATION_REMINDER)
			.content("60")
			.build();

		given(notificationCommandService.save(any(Notification.class))).willReturn(notification);

		// when
		notificationFacadeService.sendNotification(reqDto);

		// then
		verify(notificationCommandService).save(any(Notification.class));
		verify(eventPublisher).publishEvent(any(NotificationEvent.class));
	}

	@Test
	@DisplayName("알림 목록 조회를 위임하고 변환하여 반환한다")
	void getNotifications_Success() {
		// given
		Long userId = 1L;
		
		Notification notification = Notification.builder()
			.id(1L)
			.userId(userId)
			.type(NotificationType.RESERVATION_REMINDER)
			.content("60")
			.build();
			
		List<Notification> notifications = List.of(notification);
		
		given(notificationQueryService.getNotifications(userId)).willReturn(notifications);

		// when
		List<NotificationResDto> result = notificationFacadeService.getNotifications(userId);

		// then
		assertThat(result).hasSize(1);
		assertThat(result.getFirst().notificationId()).isEqualTo(notification.getId());
		assertThat(result.getFirst().content()).isEqualTo(notification.getContent());
		verify(notificationQueryService).getNotifications(userId);
	}

	@Test
	@DisplayName("알림 삭제를 위임한다")
	void deleteNotification_Success() {
		// given
		Long userId = 1L;
		Long notificationId = 100L;

		// when
		notificationFacadeService.deleteNotification(userId, notificationId);

		// then
		verify(notificationCommandService).delete(notificationId, userId);
	}
}
