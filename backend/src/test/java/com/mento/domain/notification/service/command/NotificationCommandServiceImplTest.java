package com.mento.domain.notification.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.common.error.ErrorCode;
import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.exception.NotificationException;
import com.mento.domain.notification.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationCommandServiceImplTest {

	@InjectMocks
	private NotificationCommandServiceImpl notificationCommandService;

	@Mock
	private NotificationRepository notificationRepository;

	@Test
	@DisplayName("알림을 성공적으로 발송(저장)한다")
	void 알림을_성공적으로_발송한다() {
		// given
		NotificationSendReqDto reqDto = new NotificationSendReqDto(
			1L,
			NotificationType.RESERVATION_REMINDER,
			"제목",
			"메시지",
			"/url",
			LocalDateTime.now().plusDays(1)
		);

		Notification notification = Notification.builder()
			.id(100L)
			.userId(1L)
			.type(NotificationType.RESERVATION_REMINDER)
			.build();

		given(notificationRepository.save(any(Notification.class))).willReturn(notification);

		// when
		Notification result = notificationCommandService.send(reqDto);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(100L);
		assertThat(result.getUserId()).isEqualTo(1L);
		verify(notificationRepository, times(1)).save(any(Notification.class));
	}

	@Test
	@DisplayName("알림을 성공적으로 삭제한다")
	void 알림을_성공적으로_삭제한다() {
		// given
		Long userId = 1L;
		Long notificationId = 100L;
		Notification notification = Notification.builder()
			.id(notificationId)
			.userId(userId)
			.build();

		given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

		// when
		notificationCommandService.delete(notificationId, userId);

		// then
		verify(notificationRepository, times(1)).delete(notification);
	}

	@Test
	@DisplayName("존재하지 않는 알림 삭제 시 예외가 발생한다")
	void 존재하지_않는_알림_삭제_시_예외가_발생한다() {
		// given
		Long userId = 1L;
		Long notificationId = 999L;

		given(notificationRepository.findById(notificationId)).willReturn(Optional.empty());

		// when & then
		assertThatThrownBy(() -> notificationCommandService.delete(notificationId, userId))
			.isInstanceOf(NotificationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOTIFICATION_NOT_FOUND);
	}

	@Test
	@DisplayName("다른 사용자의 알림을 삭제하려고 하면 예외가 발생한다")
	void 다른_사용자의_알림을_삭제하려고_하면_예외가_발생한다() {
		// given
		Long userId = 1L;
		Long otherUserId = 2L;
		Long notificationId = 100L;
		Notification notification = Notification.builder()
			.id(notificationId)
			.userId(otherUserId) // 소유자가 다름
			.build();

		given(notificationRepository.findById(notificationId)).willReturn(Optional.of(notification));

		// when & then
		assertThatThrownBy(() -> notificationCommandService.delete(notificationId, userId))
			.isInstanceOf(NotificationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.ACCESS_DENIED);
	}
}
