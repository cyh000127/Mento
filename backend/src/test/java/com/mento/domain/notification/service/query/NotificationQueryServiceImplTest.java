package com.mento.domain.notification.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verify;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;

import com.mento.domain.notification.dto.response.NotificationResDto;
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
		Pageable pageable = PageRequest.of(0, 10);

		Notification notification = Notification.builder()
			.id(1L)
			.userId(userId)
			.type(NotificationType.RESERVATION_REMINDER)
			.title("Title")
			.content("Content")
			.url("/url")
			.build();

		Slice<Notification> notificationSlice = new SliceImpl<>(List.of(notification));

		given(notificationRepository.findAllByUserId(userId, pageable)).willReturn(notificationSlice);

		// when
		Slice<NotificationResDto> result = notificationQueryService.getNotifications(userId, pageable);

		// then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(1);
		assertThat(result.getContent().get(0).title()).isEqualTo("Title");
		verify(notificationRepository, times(1)).findAllByUserId(userId, pageable);
	}
}
