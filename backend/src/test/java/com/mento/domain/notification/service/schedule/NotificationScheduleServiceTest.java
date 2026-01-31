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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.notification.dto.request.NotificationSendReqDto;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.notification.service.NotificationFacadeService;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.repository.ReservationRepository;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.repository.TimetableRepository;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class NotificationScheduleServiceTest {

	@InjectMocks
	private NotificationScheduleService notificationScheduleService;

	@Mock
	private NotificationFacadeService notificationFacadeService;

	@Mock
	private TimetableRepository timetableRepository;

	@Mock
	private ReservationRepository reservationRepository;

	private MockedStatic<LocalDateTime> localDateTimeMock;

	@BeforeEach
	void setUp() {
		localDateTimeMock = mockStatic(LocalDateTime.class, Mockito.CALLS_REAL_METHODS);
	}

	@AfterEach
	void tearDown() {
		localDateTimeMock.close();
	}

	@Test
	@DisplayName("상담 60분 전 알림: 만료 시간은 상담 시작 30분 전까지여야 한다")
	void 상담_60분_전_알림() {
		// given
		LocalDate today = LocalDate.of(2026, 1, 30);
		LocalTime nowTime = LocalTime.of(9, 5);
		LocalDateTime fixedNow = LocalDateTime.of(today, nowTime);
		
		localDateTimeMock.when(LocalDateTime::now).thenReturn(fixedNow);

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

		given(timetableRepository.findByScheduledDateAndScheduledTime(today, scheduledTime))
			.willReturn(List.of(timetable));


		given(reservationRepository.findAllByTimetableIdIn(List.of(1L)))
			.willReturn(List.of(reservation));

		// when
		notificationScheduleService.scheduleConsultationReminders();

		// then
		ArgumentCaptor<NotificationSendReqDto> captor = ArgumentCaptor.forClass(NotificationSendReqDto.class);
		then(notificationFacadeService).should(times(1)).sendNotification(captor.capture());

		NotificationSendReqDto reqDto = captor.getValue();
		assertThat(reqDto.type()).isEqualTo(NotificationType.RESERVATION_REMINDER);
		assertThat(reqDto.value()).isEqualTo("60");
		LocalDateTime expectedExpiredAt = LocalDateTime.of(today, scheduledTime).minusMinutes(30);
		assertThat(reqDto.expiredAt()).isEqualTo(expectedExpiredAt);
	}

	@Test
	@DisplayName("상담 30분 전 알림: 만료 시간은 상담 시작 10분 전까지여야 한다")
	void 상담_30분_전_알림() {
		// given
		LocalDate today = LocalDate.of(2026, 1, 30);
		LocalTime nowTime = LocalTime.of(9, 35);
		LocalDateTime fixedNow = LocalDateTime.of(today, nowTime);

		localDateTimeMock.when(LocalDateTime::now).thenReturn(fixedNow);

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

		given(timetableRepository.findByScheduledDateAndScheduledTime(today, scheduledTime))
			.willReturn(List.of(timetable));
		given(reservationRepository.findAllByTimetableIdIn(List.of(1L)))
			.willReturn(List.of(reservation));

		// when
		notificationScheduleService.scheduleConsultationReminders();

		// then
		ArgumentCaptor<NotificationSendReqDto> captor = ArgumentCaptor.forClass(NotificationSendReqDto.class);
		then(notificationFacadeService).should(times(1)).sendNotification(captor.capture());

		NotificationSendReqDto reqDto = captor.getValue();
		assertThat(reqDto.type()).isEqualTo(NotificationType.RESERVATION_REMINDER);
		assertThat(reqDto.value()).isEqualTo("30");
		LocalDateTime expectedExpiredAt = LocalDateTime.of(today, scheduledTime).minusMinutes(10);
		assertThat(reqDto.expiredAt()).isEqualTo(expectedExpiredAt);
	}

	@Test
	@DisplayName("상담 입장 가능(10분 전) 알림: 만료 시간은 상담 시작 10분 후까지여야 한다")
	void 상담_입장_알림() {
		// given
		LocalDate today = LocalDate.of(2026, 1, 30);
		LocalTime nowTime = LocalTime.of(9, 55);
		LocalDateTime fixedNow = LocalDateTime.of(today, nowTime);

		localDateTimeMock.when(LocalDateTime::now).thenReturn(fixedNow);

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

		given(timetableRepository.findByScheduledDateAndScheduledTime(today, scheduledTime))
			.willReturn(List.of(timetable));
		given(reservationRepository.findAllByTimetableIdIn(List.of(1L)))
			.willReturn(List.of(reservation));

		// when
		notificationScheduleService.scheduleConsultationReminders();

		// then
		ArgumentCaptor<NotificationSendReqDto> captor = ArgumentCaptor.forClass(NotificationSendReqDto.class);
		then(notificationFacadeService).should(times(1)).sendNotification(captor.capture());

		NotificationSendReqDto reqDto = captor.getValue();
		assertThat(reqDto.type()).isEqualTo(NotificationType.CONSULTING_STARTED);
		assertThat(reqDto.value()).isEqualTo("0");
		LocalDateTime expectedExpiredAt = LocalDateTime.of(today, scheduledTime).plusMinutes(10);
		assertThat(reqDto.expiredAt()).isEqualTo(expectedExpiredAt);
	}


}
