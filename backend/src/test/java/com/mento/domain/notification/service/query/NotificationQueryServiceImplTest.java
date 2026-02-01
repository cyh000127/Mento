package com.mento.domain.notification.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.repository.NotificationRepository;

@ExtendWith(MockitoExtension.class)
class NotificationQueryServiceImplTest {

	@InjectMocks
	private NotificationQueryServiceImpl notificationQueryService;

	@Mock
	private NotificationRepository notificationRepository;

	@Test
	@DisplayName("사용자의 알림 목록을 성공적으로 조회한다")
	void 사용자의_알림_목록을_성공적으로_조회한다() {
		// given
		Long userId = 1L;

		Notification notification = Notification.builder()
			.id(1L)
			.userId(userId)
			.type(NotificationType.RESERVATION_REMINDER)
			.content("60")
			.build();

		List<Notification> notificationList = List.of(notification);

		given(notificationRepository.findAllByUserId(userId)).willReturn(notificationList);

		// when
		List<Notification> result = notificationQueryService.getNotifications(userId);

		// then
		assertThat(result).isNotNull()
			.hasSize(1);
		assertThat(result.getFirst().getContent()).isEqualTo("60");
		
		verify(notificationRepository, times(1)).findAllByUserId(userId);
	}
}
