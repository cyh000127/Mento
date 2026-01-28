package com.mento.domain.reservation.service;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.ReservationException;
import com.mento.common.livekit.LiveKitManager;
import com.mento.common.livekit.dto.LiveKitSessionResponse;
import com.mento.domain.reservation.controller.query.ReservationQueryService;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.entity.ReservationStatus;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.service.query.TimetableQueryServiceImpl;
import com.mento.domain.user.entity.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationFacadeServiceTest {

	@InjectMocks
	private ReservationFacadeService reservationFacadeService;

	@Mock
	private ReservationQueryService reservationQueryService;

	@Mock
	private TimetableQueryServiceImpl timeTableQueryService;

	@Mock
	private LiveKitManager liveKitManager;

	@Test
	@DisplayName("멘토가 예약 세션에 입장한다")
	void 멘토가_예약_세션에_입장한다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentoId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentor = AuthenticatedUser.builder()
			.id(mentoId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		Reservation reservation = Reservation.builder()
			.userId(userId)
			.mentoId(mentoId)
			.timetableId(timetableId)
			.status(ReservationStatus.CONFIRMED)
			.build();
		ReflectionTestUtils.setField(reservation, "id", reservationId);

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.now())
			.scheduledTime(LocalTime.now().plusMinutes(5))
			.build();
		ReflectionTestUtils.setField(timetable, "id", timetableId);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);
		given(liveKitManager.createToken(anyString(), anyString(), anyString(), eq(Role.MENTOR), anyLong()))
			.willReturn("mock_mentor_token");
		given(liveKitManager.getUrl()).willReturn("wss://livekit.test.com");

		// When
		LiveKitSessionResponse response = reservationFacadeService.createSession(reservationId, mentor);

		// Then
		assertThat(response.timetableId()).isEqualTo(reservationId);
		assertThat(response.roomToken()).isEqualTo("mock_mentor_token");
		assertThat(response.roomName()).isEqualTo("room_1");
		assertThat(response.livekitUrl()).isEqualTo("wss://livekit.test.com");
		assertThat(response.participantRole()).isEqualTo(Role.MENTOR.getDescription());

		then(reservationQueryService).should().findById(reservationId);
		then(timeTableQueryService).should().findByReservationId(timetableId);
		then(liveKitManager).should().createToken(
			eq(String.valueOf(mentoId)),
			eq("mentor@test.com"),
			eq("room_1"),
			eq(Role.MENTOR),
			anyLong()
		);
	}

	@Test
	@DisplayName("사용자가 예약 세션에 입장한다")
	void 사용자가_예약_세션에_입장한다() {
		// Given
		Long reservationId = 2L;
		Long timetableId = 20L;
		Long mentoId = 100L;
		Long userId = 200L;

		AuthenticatedUser user = AuthenticatedUser.builder()
			.id(userId)
			.email("user@test.com")
			.role(Role.USER.name())
			.build();

		Reservation reservation = Reservation.builder()
			.userId(userId)
			.mentoId(mentoId)
			.timetableId(timetableId)
			.status(ReservationStatus.CONFIRMED)
			.build();
		ReflectionTestUtils.setField(reservation, "id", reservationId);

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.now())
			.scheduledTime(LocalTime.now().plusMinutes(5))
			.build();
		ReflectionTestUtils.setField(timetable, "id", timetableId);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);
		given(liveKitManager.createToken(anyString(), anyString(), anyString(), eq(Role.USER), anyLong()))
			.willReturn("mock_user_token");
		given(liveKitManager.getUrl()).willReturn("wss://livekit.test.com");

		// When
		LiveKitSessionResponse response = reservationFacadeService.createSession(reservationId, user);

		// Then
		assertThat(response.timetableId()).isEqualTo(reservationId);
		assertThat(response.roomToken()).isEqualTo("mock_user_token");
		assertThat(response.roomName()).isEqualTo("room_2");
		assertThat(response.livekitUrl()).isEqualTo("wss://livekit.test.com");
		assertThat(response.participantRole()).isEqualTo(Role.USER.getDescription());

		then(reservationQueryService).should().findById(reservationId);
		then(timeTableQueryService).should().findByReservationId(timetableId);
		then(liveKitManager).should().createToken(
			eq(String.valueOf(userId)),
			eq("user@test.com"),
			eq("room_2"),
			eq(Role.USER),
			anyLong()
		);
	}

	@Test
	@DisplayName("예약이 존재하지 않으면 예외가 발생한다")
	void 예약이_존재하지_않으면_예외가_발생한다() {
		// Given
		Long reservationId = 999L;
		AuthenticatedUser user = AuthenticatedUser.builder()
			.id(1L)
			.email("test@test.com")
			.role(Role.USER.name())
			.build();

		given(reservationQueryService.findById(reservationId))
			.willThrow(new ReservationException(ErrorCode.RESERVATION_NOT_FOUND));

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, user))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_NOT_FOUND);

		then(reservationQueryService).should().findById(reservationId);
		then(timeTableQueryService).should(never()).findByReservationId(any());
	}

	@Test
	@DisplayName("상담 시작 10분 전보다 이른 시간에는 입장할 수 없다")
	void 상담_시작_10분_전보다_이른_시간에는_입장할_수_없다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentoId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentor = AuthenticatedUser.builder()
			.id(mentoId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		Reservation reservation = Reservation.builder()
			.userId(userId)
			.mentoId(mentoId)
			.timetableId(timetableId)
			.status(ReservationStatus.CONFIRMED)
			.build();

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.now())
			.scheduledTime(LocalTime.now().plusMinutes(15)) // 15분 후 (10분 이상 남음)
			.build();

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, mentor))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_STARTED_YET);
	}

	@Test
	@DisplayName("종료된 상담에는 입장할 수 없다")
	void 종료된_상담에는_입장할_수_없다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentoId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentor = AuthenticatedUser.builder()
			.id(mentoId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		Reservation reservation = Reservation.builder()
			.userId(userId)
			.mentoId(mentoId)
			.timetableId(timetableId)
			.status(ReservationStatus.COMPLETED)
			.build();

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.now().minusDays(1)) // 어제
			.scheduledTime(LocalTime.now())
			.build();

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, mentor))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONSULTING_ENDED);
	}


	@Test
	@DisplayName("토큰 Ttl이 0 이하면 예외가 발생한다")
	void 토큰_Ttl이_0_이하면_예외가_발생한다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentoId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentor = AuthenticatedUser.builder()
			.id(mentoId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		Reservation reservation = Reservation.builder()
			.userId(userId)
			.mentoId(mentoId)
			.timetableId(timetableId)
			.status(ReservationStatus.CONFIRMED)
			.build();

		// 시작 시간 + 10분(END_MINUTES)보다 이전 = TTL이 0 이하
		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.now())
			.scheduledTime(LocalTime.now().minusMinutes(15))
			.build();

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, mentor))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONSULTING_ENDED);
	}
}
