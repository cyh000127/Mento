package com.mento.domain.reservation.service;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
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
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.notification.service.NotificationFacadeService;
import com.mento.domain.reservation.dto.request.ReservationHistoryReqDto;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.dto.response.ReservationDraftResDto;
import com.mento.domain.reservation.dto.response.ReservationPageInfoDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.reservation.factory.ReservationFactory;
import com.mento.domain.reservation.service.command.ReservationCommandService;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.reservation.validator.ReservationValidator;
import com.mento.domain.timetable.entity.SlotStatus;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.service.query.TimetableQueryService;
import com.mento.domain.timetable.service.query.TimetableSlotQueryService;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

@ExtendWith(MockitoExtension.class)
class ReservationFacadeServiceTest {

	@InjectMocks
	private ReservationFacadeService reservationFacadeService;

	@Mock
	private ReservationQueryService reservationQueryService;

	@Mock
	private ReservationCommandService reservationCommandService;

	@Mock
	private TimetableQueryService timeTableQueryService;

	@Mock
	private TimetableSlotQueryService timetableSlotQueryService;

	@Mock
	private UserQueryServiceImpl userQueryService;

	@Mock
	private LiveKitManager liveKitManager;

	@Mock
	private ReservationValidator reservationValidator;

	@Mock
	private ReservationFactory reservationFactory;

	@Mock
	private NotificationFacadeService notificationFacadeService;

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
		User mentor = createMentorUser(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now(), LocalTime.now().plusMinutes(5));
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
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
		then(liveKitManager).should().createToken(
			eq(String.format("%s(%s)", mentorId, Role.MENTOR.name())),
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
		User mentor = createMentorUser(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now(), LocalTime.now().plusMinutes(5));
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
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
		then(liveKitManager).should().createToken(
			eq(String.format("%s(%s)", userId, Role.USER.name())),
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
	}

	// @Test
	// @DisplayName("상담_시작_10분_전보다_이른_시간에는_입장할_수_없다")
	// void 상담_시작_10분_전보다_이른_시간에는_입장할_수_없다() {
	// 	// Given
	// 	Long reservationId = 1L;
	// 	Long timetableId = 10L;
	// 	Long mentorId = 100L;
	// 	Long userId = 200L;
	//
	// 	AuthenticatedUser mentorAuth = AuthenticatedUser.builder()
	// 		.id(mentorId)
	// 		.email("mentor@test.com")
	// 		.role(Role.MENTOR.name())
	// 		.build();
	//
	// 	User user = createUser(userId);
	// 	Mentor mentor = createMentor(mentorId);
	// 	Timetable timetable = createTimetable(timetableId, LocalDate.now(), LocalTime.now().plusMinutes(15));
	// 	TimetableSlot slot = createSlot(1L, timetable);
	// 	Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);
	//
	// 	given(reservationQueryService.findById(reservationId)).willReturn(reservation);
	//
	// 	// When & Then
	// 	assertThatThrownBy(() -> reservationFacadeService.createSession(reservationId, mentorAuth))
	// 		.isInstanceOf(ReservationException.class)
	// 		.hasFieldOrPropertyWithValue("errorCode", ErrorCode.NOT_STARTED_YET);
	// }

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
		User mentor = createMentorUser(mentorId);
		Timetable timetable = createTimetable(timetableId, LocalDate.now().minusDays(1), LocalTime.now());
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.COMPLETED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);

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
		User mentor = createMentorUser(mentorId);

		ZoneId seoulZone = ZoneId.of("Asia/Seoul");
		LocalDateTime baseTime = LocalDateTime.now(seoulZone).minusMinutes(15);
		Timetable timetable = createTimetable(
			timetableId,
			baseTime.toLocalDate(),
			baseTime.toLocalTime()
		);
		TimetableSlot slot = createSlot(1L, timetable);
		Reservation reservation = createReservation(reservationId, user, mentor, slot, ReservationStatus.CONFIRMED);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);

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
		ReservationHistoryReqDto reqDto = ReservationHistoryReqDto.builder()
			.startDate(LocalDate.of(2026, 1, 1))
			.endDate(LocalDate.of(2026, 1, 31))
			.status(ReservationStatus.CONFIRMED)
			.page(0)
			.size(10)
			.build();

		List<Reservation> reservations = List.of(
			createReservationWithFullDetails(1L, userId),
			createReservationWithFullDetails(2L, userId)
		);
		Page<Reservation> reservationPage = new PageImpl<>(reservations, PageRequest.of(0, 10),
			reservations.size());

		given(reservationQueryService.findAllByRoleAndIdAndStatusWithPageable(
			eq(userId), eq(Role.USER), eq(reqDto.status()), eq(reqDto.startDate()), eq(reqDto.endDate()), any()
		)).willReturn(reservationPage);

		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.role(Role.USER.name())
			.build();

		// When
		Page<ReservationPageInfoDto> result = reservationFacadeService.findAllByAuthUserAndDateRange(authUser, reqDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2);

		ReservationPageInfoDto first = result.getContent().get(0);
		assertThat(first.reservationId()).isEqualTo(1L);
		assertThat(first.scheduledDate()).isNotNull();
		assertThat(first.mentorType()).isNotNull();
		assertThat(first.status()).isEqualTo(ReservationStatus.CONFIRMED);

		then(reservationQueryService).should().findAllByRoleAndIdAndStatusWithPageable(
			eq(userId), eq(Role.USER), eq(reqDto.status()), eq(reqDto.startDate()), eq(reqDto.endDate()), any()
		);
	}

	@Test
	@DisplayName("파라미터_없이_예약_목록_조회_성공_전체조회_최신순")
	void 파라미터_없이_예약_목록_조회_성공_전체조회_최신순() {
		// Given
		Long userId = 1L;
		ReservationHistoryReqDto reqDto = ReservationHistoryReqDto.builder().build();

		List<Reservation> reservations = List.of(
			createReservationWithFullDetails(3L, userId),
			createReservationWithFullDetails(2L, userId),
			createReservationWithFullDetails(1L, userId)
		);
		Page<Reservation> reservationPage = new PageImpl<>(reservations, PageRequest.of(0, 10),
			reservations.size());

		given(reservationQueryService.findAllByRoleAndIdAndStatusWithPageable(
			eq(userId), eq(Role.USER), isNull(), isNull(), isNull(), any()
		)).willReturn(reservationPage);

		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.role(Role.USER.name())
			.build();

		// When
		Page<ReservationPageInfoDto> result = reservationFacadeService.findAllByAuthUserAndDateRange(authUser, reqDto);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(3);
		assertThat(result.getTotalElements()).isEqualTo(3);

		// ID 내림차순 확인 (최신순)
		assertThat(result.getContent().get(0).reservationId()).isEqualTo(3L);
		assertThat(result.getContent().get(1).reservationId()).isEqualTo(2L);
		assertThat(result.getContent().get(2).reservationId()).isEqualTo(1L);

		then(reservationQueryService).should().findAllByRoleAndIdAndStatusWithPageable(
			eq(userId), eq(Role.USER), isNull(), isNull(), isNull(), any()
		);
	}

	@Test
	@DisplayName("임시_예약_생성_성공")
	void 임시_예약_생성_성공() {
		// Given
		Long userId = 1L;
		Long slotId = 10L;
		Long reservationId = 100L;
		LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15);

		User user = createUser(userId);
		Timetable timetable = createTimetable(1L, LocalDate.now().plusDays(1), LocalTime.of(14, 0));
		MentorType mentorType = createMentorType(1L, "스킨케어");
		TimetableSlot timetableSlot = createSlotWithMentorType(slotId, timetable, mentorType, SlotStatus.AVAILABLE, 0,
			5);
		Reservation reservation = createReservationWithExpiry(reservationId, user, null, timetableSlot,
			ReservationStatus.IN_PROGRESS, expiresAt);

		given(userQueryService.findById(userId)).willReturn(user);
		given(timetableSlotQueryService.findById(slotId)).willReturn(timetableSlot);
		given(reservationQueryService.existsByUserIdAndSlotIdAndStatusIn(
			userId, slotId, ReservationStatus.getActiveStatuses()
		)).willReturn(false);
		given(reservationFactory.createReservation(user, timetableSlot)).willReturn(reservation);
		given(reservationCommandService.save(reservation)).willReturn(reservation);

		// When
		ReservationDraftResDto result = reservationFacadeService.createDraftReservation(userId, slotId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.reservationId()).isEqualTo(reservationId);
		assertThat(result.status()).isEqualTo(ReservationStatus.IN_PROGRESS);
		assertThat(result.expiresAt()).isNotNull();

		then(userQueryService).should().findById(userId);
		then(timetableSlotQueryService).should().findById(slotId);
		then(reservationQueryService).should().existsByUserIdAndSlotIdAndStatusIn(
			eq(userId), eq(slotId), eq(ReservationStatus.getActiveStatuses())
		);
		then(reservationFactory).should().createReservation(user, timetableSlot);
		then(reservationCommandService).should().save(reservation);
	}

	@Test
	@DisplayName("임시_예약_생성_실패_슬롯이_이용_불가능함")
	void 임시_예약_생성_실패_슬롯이_이용_불가능함() {
		// Given
		Long userId = 1L;
		Long slotId = 10L;

		User user = createUser(userId);
		Timetable timetable = createTimetable(1L, LocalDate.now().plusDays(1), LocalTime.of(14, 0));
		MentorType mentorType = createMentorType(1L, "스킨케어");
		TimetableSlot timetableSlot = createSlotWithMentorType(slotId, timetable, mentorType, SlotStatus.FULL, 5, 5);

		given(userQueryService.findById(userId)).willReturn(user);
		given(timetableSlotQueryService.findById(slotId)).willReturn(timetableSlot);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createDraftReservation(userId, slotId))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.TIMETABLE_NOT_AVAILABLE);

		then(userQueryService).should().findById(userId);
		then(timetableSlotQueryService).should().findById(slotId);
		then(reservationQueryService).should(never()).existsByUserIdAndSlotIdAndStatusIn(any(), any(), any());
		then(reservationFactory).should(never()).createReservation(any(), any());
		then(reservationCommandService).should(never()).save(any());
	}

	@Test
	@DisplayName("임시_예약_생성_실패_과거_시간대_슬롯")
	void 임시_예약_생성_실패_과거_시간대_슬롯() {
		// Given
		Long userId = 1L;
		Long slotId = 10L;

		User user = createUser(userId);
		Timetable timetable = createTimetable(1L, LocalDate.now().minusDays(1), LocalTime.of(14, 0));
		MentorType mentorType = createMentorType(1L, "스킨케어");
		TimetableSlot timetableSlot = createSlotWithMentorType(slotId, timetable, mentorType, SlotStatus.AVAILABLE, 0,
			5);

		given(userQueryService.findById(userId)).willReturn(user);
		given(timetableSlotQueryService.findById(slotId)).willReturn(timetableSlot);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createDraftReservation(userId, slotId))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.TIMETABLE_PAST_TIME);

		then(userQueryService).should().findById(userId);
		then(timetableSlotQueryService).should().findById(slotId);
		then(reservationQueryService).should(never()).existsByUserIdAndSlotIdAndStatusIn(any(), any(), any());
		then(reservationFactory).should(never()).createReservation(any(), any());
		then(reservationCommandService).should(never()).save(any());
	}

	@Test
	@DisplayName("임시_예약_생성_실패_중복_예약_존재")
	void 임시_예약_생성_실패_중복_예약_존재() {
		// Given
		Long userId = 1L;
		Long slotId = 10L;

		User user = createUser(userId);
		Timetable timetable = createTimetable(1L, LocalDate.now().plusDays(1), LocalTime.of(14, 0));
		MentorType mentorType = createMentorType(1L, "스킨케어");
		TimetableSlot timetableSlot = createSlotWithMentorType(slotId, timetable, mentorType, SlotStatus.AVAILABLE, 0,
			5);

		given(userQueryService.findById(userId)).willReturn(user);
		given(timetableSlotQueryService.findById(slotId)).willReturn(timetableSlot);
		given(reservationQueryService.existsByUserIdAndSlotIdAndStatusIn(userId, slotId,
			ReservationStatus.getActiveStatuses()
		)).willReturn(true);

		// When & Then
		assertThatThrownBy(() -> reservationFacadeService.createDraftReservation(userId, slotId))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.DUPLICATE_RESERVATION);

		then(userQueryService).should().findById(userId);
		then(timetableSlotQueryService).should().findById(slotId);
		then(reservationQueryService).should().existsByUserIdAndSlotIdAndStatusIn(
			eq(userId), eq(slotId), eq(ReservationStatus.getActiveStatuses())
		);
		then(reservationFactory).should(never()).createReservation(any(), any());
		then(reservationCommandService).should(never()).save(any());
	}

	@Test
	@DisplayName("예약_설문조사_데이터_업데이트_성공")
	void 예약_설문조사_데이터_업데이트_성공() {
		// Given
		Long reservationId = 1L;
		Long userId = 100L;
		String surveyData = "{\"question1\":\"answer1\",\"question2\":\"answer2\"}";

		AuthenticatedUser authUser = AuthenticatedUser.builder()
			.id(userId)
			.email("user@test.com")
			.role(Role.USER.name())
			.build();

		Reservation reservation = createReservationWithFullDetails(reservationId, userId);

		given(reservationQueryService.findById(reservationId)).willReturn(reservation);
		willDoNothing().given(reservationValidator).validateReservationAccess(authUser, reservation);

		// When
		ReservationDetailResDto result = reservationFacadeService.updateReservationSurveyData(
			authUser, reservationId, surveyData
		);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.reservationId()).isEqualTo(reservationId);

		then(reservationQueryService).should().findById(reservationId);
		then(reservationValidator).should().validateReservationAccess(authUser, reservation);
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

	private User createMentorUser(final Long mentorId) {
		MentorType mentorType = createMentorType(1L, "스킨케어");
		User mentor = User.builder()
			.name("테스트 멘토")
			.email("mentor@test.com")
			.password("password")
			.kakaoId("mentor_kakao")
			.role(Role.MENTOR)
			.mentorType(mentorType)
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

	private TimetableSlot createSlotWithMentorType(
		final Long slotId,
		final Timetable timetable,
		final MentorType mentorType,
		final SlotStatus status,
		final Integer currentCapacity,
		final Integer maxCapacity
	) {
		TimetableSlot slot = TimetableSlot.builder()
			.timetable(timetable)
			.mentorType(mentorType)
			.status(status)
			.currentCapacity(currentCapacity)
			.maxCapacity(maxCapacity)
			.build();
		ReflectionTestUtils.setField(slot, "id", slotId);
		return slot;
	}

	private MentorType createMentorType(final Long typeId, final String typeName) {
		MentorType mentorType = MentorType.builder()
			.typeName(typeName)
			.build();
		ReflectionTestUtils.setField(mentorType, "id", typeId);
		return mentorType;
	}

	private Reservation createReservationWithExpiry(
		final Long reservationId,
		final User user,
		final User mentor,
		final TimetableSlot slot,
		final ReservationStatus status,
		final LocalDateTime expiresAt
	) {
		Reservation reservation = Reservation.builder()
			.user(user)
			.mentor(mentor)
			.slot(slot)
			.status(status)
			.expiresAt(expiresAt)
			.build();
		ReflectionTestUtils.setField(reservation, "id", reservationId);
		return reservation;
	}

	private Reservation createReservation(
		final Long reservationId,
		final User user,
		final User mentor,
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
		User mentor = createMentorUser(1L);

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
