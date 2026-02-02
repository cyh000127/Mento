package com.mento.domain.notification.service.schedule;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import com.mento.common.util.TimeUtils;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.event.NotificationEvent;
import com.mento.domain.notification.service.command.NotificationCommandService;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class NotificationScheduleServiceTest {

	@InjectMocks
	private NotificationScheduleService notificationScheduleService;

	@Mock
	private NotificationCommandService notificationCommandService;

	@Mock
	private ApplicationEventPublisher eventPublisher;

	@Mock
	private TimetableQueryService timetableQueryService;

	@Mock
	private ReservationQueryService reservationQueryService;

	@Captor
	private ArgumentCaptor<List<Notification>> captor;

	private MockedStatic<TimeUtils> timeUtilsMock;

	@BeforeEach
	void setUp() {
		timeUtilsMock = mockStatic(TimeUtils.class);
	}

	@AfterEach
	void tearDown() {
		timeUtilsMock.close();
	}

	@Test
	@DisplayName("상담 60분 전 알림: 만료 시간은 상담 시작 30분 전까지여야 한다")
	void 상담_60분_전_알림() {
		// given
		LocalDate today = LocalDate.of(2026, 1, 30);
		LocalTime nowTime = LocalTime.of(9, 5);
		LocalDateTime fixedNow = LocalDateTime.of(today, nowTime);

		timeUtilsMock.when(TimeUtils::nowAsLocalDateTime).thenReturn(fixedNow);

		LocalTime scheduledTime = LocalTime.of(10, 0);
		Timetable timetable = Timetable.builder()
			.id(1L)
			.scheduledDate(today)
			.scheduledTime(scheduledTime)
			.build();

		MentorType mentorType = MentorType.builder()
			.id(1L)
			.typeName("스킨케어")
			.price(35000)
			.build();

		TimetableSlot slot = TimetableSlot.builder()
			.id(1L)
			.timetable(timetable)
			.mentorType(mentorType)
			.build();

		User user = User.builder()
			.id(1L)
			.build();

		Reservation reservation = Reservation.builder()
			.id(100L)
			.user(user)
			.slot(slot)
			.build();

		given(timetableQueryService.findAllByDateAndTime(today, scheduledTime))
			.willReturn(List.of(timetable));

		given(reservationQueryService.findAllByTimetableIds(List.of(1L)))
			.willReturn(List.of(reservation));

		given(notificationCommandService.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

		// when
		notificationScheduleService.scheduleConsultationReminders();

		// then
		then(notificationCommandService).should(times(1)).saveAll(captor.capture());
		then(eventPublisher).should(times(1)).publishEvent(any(NotificationEvent.class));

		List<Notification> reqDtos = captor.getValue();
		assertThat(reqDtos).hasSize(1);
		Notification reqDto = reqDtos.get(0);

		assertThat(reqDto.getType()).isEqualTo(NotificationType.RESERVATION_REMINDER);
		assertThat(reqDto.getContent()).isEqualTo("60");
		LocalDateTime expectedExpiredAt = LocalDateTime.of(today, scheduledTime).minusMinutes(30);
		assertThat(reqDto.getExpiredAt()).isEqualTo(expectedExpiredAt);
	}

	@Test
	@DisplayName("상담 30분 전 알림: 만료 시간은 상담 시작 10분 전까지여야 한다")
	void 상담_30분_전_알림() {
		// given
		LocalDate today = LocalDate.of(2026, 1, 30);
		LocalTime nowTime = LocalTime.of(9, 35);
		LocalDateTime fixedNow = LocalDateTime.of(today, nowTime);

		timeUtilsMock.when(TimeUtils::nowAsLocalDateTime).thenReturn(fixedNow);

		LocalTime scheduledTime = LocalTime.of(10, 0);
		Timetable timetable = Timetable.builder()
			.id(1L)
			.scheduledDate(today)
			.scheduledTime(scheduledTime)
			.build();

		MentorType mentorType = MentorType.builder()
			.id(1L)
			.typeName("스킨케어")
			.price(35000)
			.build();

		TimetableSlot slot = TimetableSlot.builder()
			.id(1L)
			.timetable(timetable)
			.mentorType(mentorType)
			.build();

		User user = User.builder()
			.id(1L)
			.build();

		Reservation reservation = Reservation.builder()
			.id(100L)
			.user(user)
			.slot(slot)
			.build();

		given(timetableQueryService.findAllByDateAndTime(today, scheduledTime))
			.willReturn(List.of(timetable));
		given(reservationQueryService.findAllByTimetableIds(List.of(1L)))
			.willReturn(List.of(reservation));

		given(notificationCommandService.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

		// when
		notificationScheduleService.scheduleConsultationReminders();

		// then
		then(notificationCommandService).should(times(1)).saveAll(captor.capture());
		then(eventPublisher).should(times(1)).publishEvent(any(NotificationEvent.class));

		List<Notification> reqDtos = captor.getValue();
		assertThat(reqDtos).hasSize(1);
		Notification reqDto = reqDtos.get(0);

		assertThat(reqDto.getType()).isEqualTo(NotificationType.RESERVATION_REMINDER);
		assertThat(reqDto.getContent()).isEqualTo("30");
		LocalDateTime expectedExpiredAt = LocalDateTime.of(today, scheduledTime).minusMinutes(10);
		assertThat(reqDto.getExpiredAt()).isEqualTo(expectedExpiredAt);
	}

	@Test
	@DisplayName("상담 입장 가능(10분 전) 알림: 만료 시간은 상담 시작 10분 후까지여야 한다")
	void 상담_입장_알림() {
		// given
		LocalDate today = LocalDate.of(2026, 1, 30);
		LocalTime nowTime = LocalTime.of(9, 55);
		LocalDateTime fixedNow = LocalDateTime.of(today, nowTime);

		timeUtilsMock.when(TimeUtils::nowAsLocalDateTime).thenReturn(fixedNow);

		LocalTime scheduledTime = LocalTime.of(10, 0);
		Timetable timetable = Timetable.builder()
			.id(1L)
			.scheduledDate(today)
			.scheduledTime(scheduledTime)
			.build();

		MentorType mentorType = MentorType.builder()
			.id(1L)
			.typeName("스킨케어")
			.price(35000)
			.build();

		TimetableSlot slot = TimetableSlot.builder()
			.id(1L)
			.timetable(timetable)
			.mentorType(mentorType)
			.build();

		User user = User.builder()
			.id(1L)
			.build();

		Reservation reservation = Reservation.builder()
			.id(100L)
			.user(user)
			.slot(slot)
			.build();

		given(timetableQueryService.findAllByDateAndTime(today, scheduledTime))
			.willReturn(List.of(timetable));
		given(reservationQueryService.findAllByTimetableIds(List.of(1L)))
			.willReturn(List.of(reservation));

		given(notificationCommandService.saveAll(anyList())).willAnswer(invocation -> invocation.getArgument(0));

		// when
		notificationScheduleService.scheduleConsultationReminders();

		// then
		then(notificationCommandService).should(times(1)).saveAll(captor.capture());
		then(eventPublisher).should(times(1)).publishEvent(any(NotificationEvent.class));

		List<Notification> reqDtos = captor.getValue();
		assertThat(reqDtos).hasSize(1);
		Notification reqDto = reqDtos.get(0);

		assertThat(reqDto.getType()).isEqualTo(NotificationType.CONSULTING_STARTED);
		assertThat(reqDto.getContent()).isEqualTo("0");
		LocalDateTime expectedExpiredAt = LocalDateTime.of(today, scheduledTime).plusMinutes(10);
		assertThat(reqDto.getExpiredAt()).isEqualTo(expectedExpiredAt);
	}

}
