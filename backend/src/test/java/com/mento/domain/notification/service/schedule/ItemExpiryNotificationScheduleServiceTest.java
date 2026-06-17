package com.mento.domain.notification.service.schedule;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.mento.domain.item.dto.common.ExpiringItemCountDto;
import com.mento.domain.item.service.query.ItemQueryService;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.service.command.NotificationCommandService;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class ItemExpiryNotificationScheduleServiceTest {

	@Mock
	private ItemQueryService itemQueryService;

	@Mock
	private NotificationCommandService notificationCommandService;

	@InjectMocks
	private ItemExpiryNotificationScheduleService scheduleService;

	@Captor
	private ArgumentCaptor<List<Notification>> notificationCaptor;

	private User user1;
	private User user2;

	@BeforeEach
	void setUp() {
		user1 = User.builder()
			.name("테스트 사용자1")
			.email("user1@test.com")
			.build();
		ReflectionTestUtils.setField(user1, "id", 1L);

		user2 = User.builder()
			.name("테스트 사용자2")
			.email("user2@test.com")
			.build();
		ReflectionTestUtils.setField(user2, "id", 2L);
	}

	@Test
	@DisplayName("만료 예정 아이템이_있을때_알림이_DB에_저장된다")
	void 만료_예정_아이템이_있을때_알림이_DB에_저장된다() {
		// Given
		List<ExpiringItemCountDto> expiringItemCounts = Arrays.asList(
			new ExpiringItemCountDto(user1, 2L),
			new ExpiringItemCountDto(user2, 1L)
		);

		given(itemQueryService.countExpiringItemsByUserBetween(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(expiringItemCounts);

		// When
		scheduleService.checkAndNotifyExpiringItems();

		// Then
		then(notificationCommandService).should(times(1)).saveAll(notificationCaptor.capture());

		List<Notification> capturedNotifications = notificationCaptor.getValue();
		assertThat(capturedNotifications).hasSize(2); // 2명의 사용자

		// user1은 2개 아이템
		Notification user1Notification = capturedNotifications.stream()
			.filter(n -> n.getUserId().equals(1L))
			.findFirst()
			.orElseThrow();
		assertThat(user1Notification.getType()).isEqualTo(NotificationType.INVENTORY_EXPIRY);
		assertThat(user1Notification.getContent()).isEqualTo("2");
		assertThat(user1Notification.getExpiredAt()).isAfter(LocalDateTime.now());

		// user2는 1개 아이템
		Notification user2Notification = capturedNotifications.stream()
			.filter(n -> n.getUserId().equals(2L))
			.findFirst()
			.orElseThrow();
		assertThat(user2Notification.getType()).isEqualTo(NotificationType.INVENTORY_EXPIRY);
		assertThat(user2Notification.getContent()).isEqualTo("1");
	}

	@Test
	@DisplayName("만료_예정_아이템이_없으면_알림을_보내지_않는다")
	void 만료_예정_아이템이_없으면_알림을_보내지_않는다() {
		// Given
		given(itemQueryService.countExpiringItemsByUserBetween(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(List.of());

		// When
		scheduleService.checkAndNotifyExpiringItems();

		// Then
		then(notificationCommandService).should(never()).saveAll(anyList());
	}

	@Test
	@DisplayName("알림_만료시간이_다음날_12시로_설정된다")
	void 알림_만료시간이_다음날_12시로_설정된다() {
		// Given
		LocalDate today = LocalDate.now();
		given(itemQueryService.countExpiringItemsByUserBetween(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(List.of(new ExpiringItemCountDto(user1, 1L)));

		LocalDateTime expectedExpiry = LocalDateTime.of(today.plusDays(1), LocalTime.of(12, 0));

		// When
		scheduleService.checkAndNotifyExpiringItems();

		// Then
		then(notificationCommandService).should(times(1)).saveAll(notificationCaptor.capture());

		List<Notification> capturedNotifications = notificationCaptor.getValue();
		assertThat(capturedNotifications).hasSize(1);
		assertThat(capturedNotifications.get(0).getExpiredAt()).isEqualTo(expectedExpiry);
	}

	@Test
	@DisplayName("7일_이내_만료_예정_아이템만_조회한다")
	void 칠일_이내_만료_예정_아이템만_조회한다() {
		// Given
		LocalDate today = LocalDate.now();
		given(itemQueryService.countExpiringItemsByUserBetween(any(LocalDate.class), any(LocalDate.class)))
			.willReturn(List.of());

		// When
		scheduleService.checkAndNotifyExpiringItems();

		// Then
		ArgumentCaptor<LocalDate> startDateCaptor = ArgumentCaptor.forClass(LocalDate.class);
		ArgumentCaptor<LocalDate> endDateCaptor = ArgumentCaptor.forClass(LocalDate.class);

		then(itemQueryService).should(times(1))
			.countExpiringItemsByUserBetween(startDateCaptor.capture(), endDateCaptor.capture());

		assertThat(startDateCaptor.getValue()).isEqualTo(today);
		assertThat(endDateCaptor.getValue()).isEqualTo(today.plusDays(7));
	}
}
