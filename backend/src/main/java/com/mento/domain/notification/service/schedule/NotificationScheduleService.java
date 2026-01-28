package com.mento.domain.notification.service.schedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

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

	private final NotificationFacadeService notificationFacadeService;
	private final TimetableRepository timetableRepository;
	private final ReservationRepository reservationRepository;

	/**
	 * 1분마다 실행되어 상담 시작 1시간 전, 30분 전, 10분 전 알림을 발송합니다.
	 */
	@Scheduled(cron = "0 * * * * *")
	@Transactional
	public void scheduleConsultationReminders() {
		LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
		
		checkAndSendReminders(now.plusHours(1), "상담 시작 1시간 전입니다.", NotificationType.CONSULTATION_REMINDER);
		
		checkAndSendReminders(now.plusMinutes(30), "상담 시작 30분 전입니다.", NotificationType.CONSULTATION_REMINDER);
	
		checkAndSendReminders(now.plusMinutes(10), 
			"상담 시작 10분 전입니다. 지금부터 입장이 가능합니다!", 
			NotificationType.CONSULTATION_REMINDER);
	}

	private void checkAndSendReminders(LocalDateTime targetDateTime, String title, NotificationType type) {
		LocalDate targetDate = targetDateTime.toLocalDate();
		LocalTime targetTime = targetDateTime.toLocalTime();

		List<Timetable> timetables = timetableRepository.findByScheduledDateAndScheduledTime(targetDate, targetTime);
		
		if (timetables.isEmpty()) {
			return;
		}

		List<Long> timetableIds = timetables.stream()
			.map(Timetable::getId)
			.toList();

		List<Reservation> reservations = reservationRepository.findAllByTimetableIdIn(timetableIds);

		for (Reservation reservation : reservations) {
			sendNotification(reservation, title, "상담 예정 시각: " + targetTime, type);
		}
	}

	private void sendNotification(Reservation reservation, String title, String content, NotificationType type) {
		try {

			String url = "/consulting/" + reservation.getId(); 
			
			NotificationSendReqDto reqDto = NotificationSendReqDto.builder()
				.targetMemberId(reservation.getUserId())
				.type(type)
				.title(title)
				.message(content)
				.url(url)
				.build();

			notificationFacadeService.sendNotification(reqDto);
			
			log.info("[Notification] 알림 전송 성공 {userId: {}, reservationId: {}, type: {}}", 
				reservation.getUserId(), reservation.getId(), type);
				
		} catch (Exception e) {
			log.error("[Notification] 알림 전송 실패 {userId: {}, reservationId: {}}",
				reservation.getUserId(),
				reservation.getId(), e);
		}
	}
}
