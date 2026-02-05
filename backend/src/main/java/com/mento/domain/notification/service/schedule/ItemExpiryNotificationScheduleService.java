package com.mento.domain.notification.service.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.util.TimeUtils;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.service.query.ItemQueryService;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.service.command.NotificationCommandService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemExpiryNotificationScheduleService {

	private static final int EXPIRY_NOTIFICATION_DAY = 7;

	private final ItemQueryService itemQueryService;
	private final NotificationCommandService notificationCommandService;

	/**
	 * 매일 12시에 만료 예정 아이템 알림을 발송합니다.
	 * 만료 예정일이 1주일 이내인 아이템을 대상으로 합니다.
	 */
	@Scheduled(cron = "0 0 12 * * *")
	@Transactional
	public void checkAndNotifyExpiringItems() {
		log.info("[ItemExpiryNotification] 만료 예정 아이템 알림 체크 시작");

		LocalDate today = TimeUtils.nowAsLocalDate();
		LocalDate oneWeekLater = today.plusDays(EXPIRY_NOTIFICATION_DAY);

		List<Item> expiringItems = itemQueryService.findItemsExpiringBetween(today, oneWeekLater);

		if (expiringItems.isEmpty()) {
			log.info("[ItemExpiryNotification] 만료 예정 아이템 없음");
			return;
		}

		Map<Long, Long> userItemCountMap = expiringItems.stream()
			.collect(Collectors.groupingBy(
				item -> item.getUser().getId(),
				Collectors.counting()
			));

		LocalDateTime nextScheduleTime = LocalDateTime.of(today.plusDays(1), LocalTime.of(12, 0));

		List<Notification> notifications = userItemCountMap.entrySet().stream()
			.map(entry -> NotificationConverter.toEntity(
				entry.getKey(),
				NotificationType.INVENTORY_EXPIRY,
				String.valueOf(entry.getValue()),
				nextScheduleTime
			))
			.toList();

		try {
			notificationCommandService.saveAll(notifications);
			log.info("[ItemExpiryNotification] 만료 예정 알림 저장 완료 {userCount: {}, totalItems: {}}", 
				userItemCountMap.size(), expiringItems.size());
		} catch (Exception e) {
			log.error("[ItemExpiryNotification] 알림 저장 실패", e);
		}
	}
}
