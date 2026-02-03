package com.mento.domain.reservation.service.query;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import com.mento.common.error.ErrorCode;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.reservation.exception.ReservationException;
import com.mento.domain.reservation.repository.ReservationRepository;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;

@ExtendWith(MockitoExtension.class)
class ReservationQueryServiceImplTest {

	@InjectMocks
	private ReservationQueryServiceImpl reservationQueryService;

	@Mock
	private ReservationRepository reservationRepository;

	@Test
	@DisplayName("예약 ID로 조회 성공")
	void 예약_ID로_조회_성공() {
		// Given
		Long reservationId = 1L;
		Reservation reservation = createReservation(reservationId, ReservationStatus.CONFIRMED);

		given(reservationRepository.findById(reservationId)).willReturn(Optional.of(reservation));

		// When
		Reservation result = reservationQueryService.findById(reservationId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(reservationId);
		assertThat(result.getStatus()).isEqualTo(ReservationStatus.CONFIRMED);

		then(reservationRepository).should().findById(reservationId);
	}

	@Test
	@DisplayName("존재하지_않는_예약_조회_시_예외_발생")
	void 존재하지_않는_예약_조회_시_예외_발생() {
		// Given
		Long reservationId = 999L;
		given(reservationRepository.findById(reservationId)).willReturn(Optional.empty());

		// When & Then
		assertThatThrownBy(() -> reservationQueryService.findById(reservationId))
			.isInstanceOf(ReservationException.class)
			.hasFieldOrPropertyWithValue("errorCode", ErrorCode.RESERVATION_NOT_FOUND);

		then(reservationRepository).should().findById(reservationId);
	}

	@Test
	@DisplayName("상세_정보_포함_예약_조회_성공")
	void 상세_정보_포함_예약_조회_성공() {
		// Given
		Long reservationId = 1L;
		Reservation reservation = createReservationWithDetails(reservationId);

		given(reservationRepository.findWithDetailsById(reservationId)).willReturn(Optional.of(reservation));

		// When
		Reservation result = reservationQueryService.findWithDetailsById(reservationId);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getId()).isEqualTo(reservationId);
		assertThat(result.getUser()).isNotNull();
		assertThat(result.getMentor()).isNotNull();
		assertThat(result.getSlot()).isNotNull();
		assertThat(result.getSlot().getTimetable()).isNotNull();
		assertThat(result.getSlot().getMentorType()).isNotNull();

		then(reservationRepository).should().findWithDetailsById(reservationId);
	}

	@Test
	@DisplayName("예약_존재_여부_확인_성공")
	void 예약_존재_여부_확인_성공() {
		// Given
		Long reservationId = 1L;
		given(reservationRepository.existsById(reservationId)).willReturn(true);

		// When
		boolean exists = reservationQueryService.existById(reservationId);

		// Then
		assertThat(exists).isTrue();
		then(reservationRepository).should().existsById(reservationId);
	}

	@Test
	@DisplayName("사용자_ID와_상태로_예약_목록_페이징_조회_성공")
	void 사용자_ID와_상태로_예약_목록_페이징_조회_성공() {
		// Given
		Long userId = 1L;
		ReservationStatus status = ReservationStatus.CONFIRMED;
		LocalDate startDate = LocalDate.of(2026, 1, 1);
		LocalDate endDate = LocalDate.of(2026, 1, 31);
		Pageable pageable = PageRequest.of(0, 10);

		List<Reservation> reservations = List.of(
			createReservation(1L, ReservationStatus.CONFIRMED),
			createReservation(2L, ReservationStatus.CONFIRMED)
		);
		Page<Reservation> expectedPage = new PageImpl<>(reservations, pageable, reservations.size());

		given(reservationRepository.findAllByUserIdAndDateRange(userId, startDate, endDate, status, pageable))
			.willReturn(expectedPage);

		// When
		Page<Reservation> result = reservationQueryService.findAllByUserIdAndStatusWithPageable(
			userId, status, startDate, endDate, pageable
		);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2);

		then(reservationRepository).should().findAllByUserIdAndDateRange(userId, startDate, endDate, status, pageable);
	}

	// Helper Methods
	private Reservation createReservation(final Long id, final ReservationStatus status) {
		Reservation reservation = Reservation.builder()
			.status(status)
			.build();
		ReflectionTestUtils.setField(reservation, "id", id);
		return reservation;
	}

	private Reservation createReservationWithDetails(final Long id) {
		User user = User.builder()
			.name("테스트 사용자")
			.email("user@test.com")
			.password("password")
			.kakaoId("kakao123")
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		MentorType mentorType = MentorType.builder()
			.typeName("스킨케어")
			.build();
		ReflectionTestUtils.setField(mentorType, "id", 1L);

		User mentor = User.builder()
			.name("테스트 멘토")
			.email("mentor@test.com")
			.password("password")
			.kakaoId("mentor_kakao")
			.role(Role.MENTOR)
			.mentorType(mentorType)
			.build();
		ReflectionTestUtils.setField(mentor, "id", 1L);

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.now())
			.scheduledTime(LocalTime.of(10, 0))
			.build();
		ReflectionTestUtils.setField(timetable, "id", 1L);

		TimetableSlot slot = TimetableSlot.builder()
			.timetable(timetable)
			.mentorType(mentorType)
			.build();
		ReflectionTestUtils.setField(slot, "id", 1L);

		Reservation reservation = Reservation.builder()
			.user(user)
			.mentor(mentor)
			.slot(slot)
			.status(ReservationStatus.CONFIRMED)
			.build();
		ReflectionTestUtils.setField(reservation, "id", id);

		return reservation;
	}
}
