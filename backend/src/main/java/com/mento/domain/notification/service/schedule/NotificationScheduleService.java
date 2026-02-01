package com.mento.domain.notification.service.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.service.NotificationFacadeService;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.service.query.TimetableQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationScheduleService {

	// TODO: 아이템 만료 알림 구현 필요
	// TODO: 컨설팅 리포트 생성 알림 구현 필요

	private final NotificationFacadeService notificationFacadeService;
	private final TimetableQueryService timetableQueryService;
	private final ReservationQueryService reservationQueryService;

	/**
	 * 5분 단위의 스케줄러가 상담 시작 전 알림을 발송합니다.
	 */
	@Scheduled(cron = "0 0/5 * * * *")
	@Transactional
	public void scheduleConsultationReminders() {
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

		int minute = now.getMinute();
		LocalTime nextHour = now.toLocalTime().plusHours(1).truncatedTo(ChronoUnit.HOURS);

		if (minute <= 25) {
			checkAndSendReminders(now.toLocalDate(), nextHour, 
				"60", NotificationType.RESERVATION_REMINDER, -30);
		}

		if (minute >= 30 && minute <= 45) {
			checkAndSendReminders(now.toLocalDate(), nextHour, 
				"30", NotificationType.RESERVATION_REMINDER, -10);
		}

		if (minute >= 50) {
			checkAndSendReminders(now.toLocalDate(), nextHour, 
				"0", NotificationType.CONSULTING_STARTED, 10);
		}
	}

	private void checkAndSendReminders(LocalDate targetDate,
		LocalTime targetTime,
		String content,
		NotificationType type,
		int expiryOffsetMinutes) {
		
		List<Timetable> timetables = timetableQueryService
			.findAllByDateAndTime(targetDate, targetTime);

		if (CollectionUtils.isEmpty(timetables)) {
			return;
		}

		List<Long> timetableIds = timetables.stream()
			.map(Timetable::getId)
			.toList();

		List<Reservation> reservations = reservationQueryService.findAllByTimetableIds(timetableIds);

		Map<Long, Timetable> timetableMap = timetables.stream()
			.collect(Collectors.toMap(Timetable::getId, Function.identity()));

		List<NotificationSendReqDto> notificationDtos = reservations.stream()
			.map(reservation -> {
				Timetable timetable = timetableMap.get(reservation.getSlot().getTimetable().getId());
				if (timetable == null) {
					return null;
				}

				LocalDateTime scheduledDateTime = LocalDateTime.of(timetable.getScheduledDate(),
					timetable.getScheduledTime());
				LocalDateTime expiredAt = scheduledDateTime.plusMinutes(expiryOffsetMinutes);

				return NotificationSendReqDto.builder()
					.targetMemberId(reservation.getUser().getId())
					.type(type)
					.content(content)
					.expiredAt(expiredAt)
					.build();
			})
			.filter(Objects::nonNull)
			.toList();

		if (!CollectionUtils.isEmpty(notificationDtos)) {
			try {
				notificationFacadeService.sendNotifications(notificationDtos);
				log.info("[Notification] 알림 일괄 전송 성공 {count: {}, type: {}}", notificationDtos.size(), type);
			} catch (Exception e) {
				log.error("[Notification] 알림 일괄 전송 실패 {type: {}}", type, e);
			}
		}
	}
}
