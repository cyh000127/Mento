package com.mento.domain.notification.service.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.service.NotificationFacadeService;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.repository.ReservationRepository;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.repository.TimetableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationScheduleService {

	// TODO: 아이템 만료 알림 구현 필요 (로그인 시 또는 매일 특정 시간에 인벤토리 확인 후 만료 1주 전 알림)
	// TODO: 컨설팅 리포트 생성 알림 구현 필요 (리포트 생성 시점에 NotificationFacadeService 호출)

	private final NotificationFacadeService notificationFacadeService;
	private final TimetableRepository timetableRepository;
	private final ReservationRepository reservationRepository;

	/**
	 * 5분 단위의 스케줄러가 상담 시작 전 알림을 발송합니다.
	 * 60분 전 (40분 ~ 60분 ): 만료시간 = 시작시간 - 30분
	 * 30분 전 (19분 ~ 30분 ): 만료시간 = 시작시간 - 10분
	 * 10분 전 (5분 ~ 15분 후 시작): 만료시간 = 시작시간 + 10분
	 */
	@Scheduled(cron = "0 0/5 * * * *")
	@Transactional
	public void scheduleConsultationReminders() {
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);

		checkAndSendReminders(now.toLocalDate(), now.toLocalTime().plusMinutes(40), now.toLocalTime().plusMinutes(60),
			"1시간 안에 상담이 시작됩니다.", NotificationType.RESERVATION_REMINDER, -30);

		checkAndSendReminders(now.toLocalDate(), now.toLocalTime().plusMinutes(20), now.toLocalTime().plusMinutes(30),
			"30분 안에 상담이 시작됩니다.", NotificationType.RESERVATION_REMINDER, -10);

		checkAndSendReminders(now.toLocalDate(), now.toLocalTime().plusMinutes(5), now.toLocalTime().plusMinutes(15),
			"지금부터 상담 입장이 가능합니다!", NotificationType.RESERVATION_REMINDER, 10);
	}

	private void checkAndSendReminders(LocalDate targetDate,
		LocalTime startTime,
		LocalTime endTime,
		String title,
		NotificationType type,
		int expiryOffsetMinutes) {
		List<Timetable> timetables = timetableRepository
			.findTimetablesInTimeRange(targetDate, startTime, endTime);

		if (timetables.isEmpty()) {
			return;
		}

		List<Long> timetableIds = timetables.stream()
			.map(Timetable::getId)
			.toList();

		List<Reservation> reservations = reservationRepository.findAllByTimetableIdIn(timetableIds);

		Map<Long, Timetable> timetableMap = timetables.stream()
			.collect(Collectors.toMap(Timetable::getId, Function.identity()));

		for (Reservation reservation : reservations) {
			Timetable timetable = timetableMap.get(reservation.getTimetableId());
			if (timetable == null) {
				continue;
			}

			LocalDateTime scheduledDateTime = LocalDateTime.of(timetable.getScheduledDate(),
				timetable.getScheduledTime());
			LocalDateTime expiredAt = scheduledDateTime.plusMinutes(expiryOffsetMinutes);

			sendNotification(reservation, title, "상담 예정 시각: " + timetable.getScheduledTime(), type, expiredAt);
		}
	}

	private void sendNotification(
		Reservation reservation,
		String title, String content,
		NotificationType type,
		LocalDateTime expiredAt) {
		try {

			String url = type.getDefaultUrl() + "/" + reservation.getId();

			NotificationSendReqDto reqDto = NotificationSendReqDto.builder()
				.targetMemberId(reservation.getUserId())
				.type(type)
				.title(title)
				.message(content)
				.url(url)
				.expiredAt(expiredAt)
				.build();

			notificationFacadeService.sendNotification(reqDto);

			log.info("[Notification] 알림 전송 성공 {userId: {}, reservationId: {}, type: {}, expiredAt: {}}",
				reservation.getUserId(), reservation.getId(), type, expiredAt);

		} catch (Exception e) {
			log.error("[Notification] 알림 전송 실패 {userId: {}, reservationId: {}}",
				reservation.getUserId(),
				reservation.getId(), e);
		}
	}
}
