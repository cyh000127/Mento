package com.mento.domain.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.ReservationException;
import com.mento.common.livekit.LiveKitManager;
import com.mento.common.livekit.dto.LiveKitSessionResponse;
import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.dto.response.ReservationPageInfoDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.entity.ReservationStatus;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.reservation.validator.ReservationValidator;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class ReservationFacadeServiceTest {

	@InjectMocks
	private ReservationFacadeService reservationFacadeService;

	@Mock
	private ReservationQueryService reservationQueryService;

	@Mock
	private TimetableQueryService timeTableQueryService;

	@Mock
	private LiveKitManager liveKitManager;

	@Mock
	private ReservationValidator reservationValidator;

	@Test
	@DisplayName("멘토가_예약_세션에_입장한다")
	void 멘토가_예약_세션에_입장한다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentorId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentorAuth = AuthenticatedUser.builder()
			.id(mentorId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		User user = createUser(userId);
		Mentor mentor = createMentor(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now(), LocalTime.now().plusMinutes(5));
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);
		given(liveKitManager.createToken(anyString(), anyString(), anyString(), eq(Role.MENTOR), anyLong()))
			.willReturn("mock_mentor_token");
		given(liveKitManager.getUrl()).willReturn("wss://livekit.test.com");

		// When
		LiveKitSessionResponse response = reservationFacadeService.createSession(reservationId, mentorAuth);

		// Then
		assertThat(response.timetableId()).isEqualTo(reservationId);
		assertThat(response.roomToken()).isEqualTo("mock_mentor_token");
		assertThat(response.roomName()).isEqualTo("room_1");
		assertThat(response.livekitUrl()).isEqualTo("wss://livekit.test.com");
		assertThat(response.participantRole()).isEqualTo(Role.MENTOR.getDescription());

		then(reservationQueryService).should().findById(reservationId);
		then(timeTableQueryService).should().findByReservationId(timetableId);
		then(liveKitManager).should().createToken(
			eq(String.valueOf(mentorId)),
			eq("mentor@test.com"),
			eq("room_1"),
			eq(Role.MENTOR),
			anyLong()
		);
	}

	@Test
	@DisplayName("사용자가_예약_세션에_입장한다")
	void 사용자가_예약_세션에_입장한다() {
		// Given
		Long reservationId = 2L;
		Long timetableId = 20L;
		Long mentorId = 100L;
		Long userId = 200L;

		AuthenticatedUser userAuth = AuthenticatedUser.builder()
			.id(userId)
			.email("user@test.com")
			.role(Role.USER.name())
			.build();

		User user = createUser(userId);
		Mentor mentor = createMentor(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now(), LocalTime.now().plusMinutes(5));
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);
		given(liveKitManager.createToken(anyString(), anyString(), anyString(), eq(Role.USER), anyLong()))
			.willReturn("mock_user_token");
		given(liveKitManager.getUrl()).willReturn("wss://livekit.test.com");

		// When
		LiveKitSessionResponse response = reservationFacadeService.createSession(reservationId, userAuth);

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
	@DisplayName("예약이_존재하지_않으면_예외가_발생한다")
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
	@DisplayName("상담_시작_10분_전보다_이른_시간에는_입장할_수_없다")
	void 상담_시작_10분_전보다_이른_시간에는_입장할_수_없다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentorId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentorAuth = AuthenticatedUser.builder()
			.id(mentorId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		User user = createUser(userId);
		Mentor mentor = createMentor(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now(), LocalTime.now().plusMinutes(15));
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, mentorAuth))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_STARTED_YET);
	}

	@Test
	@DisplayName("종료된_상담에는_입장할_수_없다")
	void 종료된_상담에는_입장할_수_없다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentorId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentorAuth = AuthenticatedUser.builder()
			.id(mentorId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		User user = createUser(userId);
		Mentor mentor = createMentor(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now().minusDays(1), LocalTime.now());
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.COMPLETED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, mentorAuth))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONSULTING_ENDED);
	}

	@Test
	@DisplayName("토큰_Ttl이_0_이하면_예외가_발생한다")
	void 토큰_Ttl이_0_이하면_예외가_발생한다() {
		// Given
		Long reservationId = 1L;
		Long timetableId = 10L;
		Long mentorId = 100L;
		Long userId = 200L;

		AuthenticatedUser mentorAuth = AuthenticatedUser.builder()
			.id(mentorId)
			.email("mentor@test.com")
			.role(Role.MENTOR.name())
			.build();

		User user = createUser(userId);
		Mentor mentor = createMentor(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now(), LocalTime.now().minusMinutes(15));
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		given(timeTableQueryService.findByReservationId(timetableId)).willReturn(timetable);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, mentorAuth))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.CONSULTING_ENDED);
	}

	@Test
	@DisplayName("예약_상세_조회_성공")
	void 예약_상세_조회_성공() {
		// Given
		Long reservationId = 1L;
		Long userId = 100L;

		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.email("user@test.com")
			.role(Role.USER.name())
			.build();

		Reservation reservation = createReservationWithFullDetails(reservationId, userId);

		given(reservationQueryService.findWithDetailsById(reservationId)).willReturn(reservation);
		willDoNothing().given(reservationValidator).validateReservationAccess(authUser, reservation);

		// When
		ReservationDetailResDto result = reservationFacadeService.findById(authUser, reservationId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.reservationId()).isEqualTo(reservationId);
		assertThat(result.userInfo()).isNotNull();
		assertThat(result.mentorInfo()).isNotNull();
		assertThat(result.mentorTypeInfo()).isNotNull();

		then(reservationQueryService).should().findWithDetailsById(reservationId);
		then(reservationValidator).should().validateReservationAccess(authUser, reservation);
	}

	@Test
	@DisplayName("사용자_ID와_날짜_범위로_예약_목록_조회_성공")
	void 사용자_ID와_날짜_범위로_예약_목록_조회_성공() {
		// Given
		Long userId = 1L;
		ReservationStatus status = ReservationStatus.CONFIRMED;
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 31);
		int page = 1;
		int size = 10;

		List<Reservation> reservations = List.of(
			createReservationWithFullDetails(1L, userId),
			createReservationWithFullDetails(2L, userId)
		);
		Page<Reservation> reservationPage = new PageImpl<>(reservations, PageRequest.of(0, size),
			reservations.size());

		given(reservationQueryService.findAllByUserIdAndStatusWithPageable(
			eq(userId), eq(status), eq(startDate), eq(endDate), any()
		)).willReturn(reservationPage);

		// When
		Page<ReservationPageInfoDto> result = reservationFacadeService.findAllByUserIdAndDateRange(
			userId, status, startDate, endDate, page, size
		);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2);

		ReservationPageInfoDto first = result.getContent().get(0);
		assertThat(first.reservationId()).isEqualTo(1L);
		assertThat(first.scheduledDate()).isNotNull();
		assertThat(first.mentorType()).isNotNull();
		assertThat(first.status()).isEqualTo(ReservationStatus.CONFIRMED);

		then(reservationQueryService).should().findAllByUserIdAndStatusWithPageable(
			eq(userId), eq(status), eq(startDate), eq(endDate), any()
		);
	}

	// Helper Methods
	private User createUser(final Long userId) {
		User user = User.builder()
			.name("테스트 사용자")
			.email("user@test.com")
			.password("password")
			.kakaoId("kakao123")
			.build();
		ReflectionTestUtils.setField(user, "id", userId);
		return user;
	}

	private Mentor createMentor(final Long mentorId) {
		Mentor mentor = Mentor.builder()
			.name("테스트 멘토")
			.loginId("mentor_login")
			.password("password")
			.build();
		ReflectionTestUtils.setField(mentor, "id", mentorId);
		return mentor;
	}

	private Timetable createTimetable(final Long timetableId, final LocalDate date, final LocalTime time) {
		Timetable timetable = Timetable.builder()
			.scheduledDate(date)
			.scheduledTime(time)
			.build();
		ReflectionTestUtils.setField(timetable, "id", timetableId);
		return timetable;
	}

	private TimetableSlot createSlot(final Long slotId, final Timetable timetable) {
		TimetableSlot slot = TimetableSlot.builder()
			.timetable(timetable)
			.build();
		ReflectionTestUtils.setField(slot, "id", slotId);
		return slot;
	}

	private Reservation createReservation(
		final Long reservationId,
		final User user,
		final Mentor mentor,
		final TimetableSlot slot,
		final ReservationStatus status
	) {
		Reservation reservation = Reservation.builder()
			.user(user)
			.mentor(mentor)
			.slot(slot)
			.status(status)
			.build();
		ReflectionTestUtils.setField(reservation, "id", reservationId);
		return reservation;
	}

	private Reservation createReservationWithFullDetails(final Long reservationId, final Long userId) {
		User user = createUser(userId);
		Mentor mentor = createMentor(1L);

		MentorType mentorType = MentorType.builder()
			.typeName("스킨케어")
			.build();
		ReflectionTestUtils.setField(mentorType, "id", 1L);

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.of(2026, 1, 30))
			.scheduledTime(LocalTime.of(10, 0))
			.build();
		ReflectionTestUtils.setField(timetable, "id", 1L);

		TimetableSlot slot = TimetableSlot.builder()
			.timetable(timetable)
			.mentorType(mentorType)
			.build();
		ReflectionTestUtils.setField(slot, "id", 1L);

		return createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);
	}
}
