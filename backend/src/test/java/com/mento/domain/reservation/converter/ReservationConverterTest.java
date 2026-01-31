package com.mento.domain.reservation.converter;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import com.mento.domain.mentor.entity.Mentor;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.dto.response.ReservationPageInfoDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.User;

class ReservationConverterTest {

	@Test
	@DisplayName("Reservation을_ReservationDetailResDto로_변환_성공")
	void reservation을_ReservationDetailResDto로_변환_성공() {
		// Given
		Reservation reservation = createReservationWithDetails(1L);

		// When
		ReservationDetailResDto result = ReservationConverter.toReservationDetailResDto(reservation);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.reservationId()).isEqualTo(1L);
		assertThat(result.userInfo()).isNotNull();
		assertThat(result.userInfo().id()).isEqualTo(1L);
		assertThat(result.userInfo().name()).isEqualTo("테스트 사용자");
		assertThat(result.mentorInfo()).isNotNull();
		assertThat(result.mentorInfo().id()).isEqualTo(2L);
		assertThat(result.mentorInfo().name()).isEqualTo("테스트 멘토");
		assertThat(result.mentorTypeInfo()).isNotNull();
		assertThat(result.mentorTypeInfo().id()).isEqualTo(3L);
		assertThat(result.mentorTypeInfo().name()).isEqualTo("스킨케어");
		assertThat(result.timetableId()).isEqualTo(10L);
		assertThat(result.scheduledDate()).isEqualTo(LocalDate.of(2026, 1, 30));
		assertThat(result.scheduledTime()).startsWith("10:00");
		assertThat(result.reservationStatus()).isEqualTo("CONFIRMED");
	}

	@Test
	@DisplayName("Reservation_Page를_ReservationPageInfoDto_Page로_변환_성공")
	void reservation_Page를_ReservationPageInfoDto_Page로_변환_성공() {
		// Given
		List<Reservation> reservations = List.of(
			createReservationWithDetails(1L),
			createReservationWithDetails(2L)
		);
		Page<Reservation> reservationPage = new PageImpl<>(reservations, PageRequest.of(0, 10), reservations.size());

		// When
		Page<ReservationPageInfoDto> result = ReservationConverter.toReservationPageResDto(reservationPage);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.getContent()).hasSize(2);
		assertThat(result.getTotalElements()).isEqualTo(2);

		ReservationPageInfoDto first = result.getContent().get(0);
		assertThat(first.reservationId()).isEqualTo(1L);
		assertThat(first.scheduledDate()).isEqualTo(LocalDate.of(2026, 1, 30));
		assertThat(first.mentorType()).isNotNull();
		assertThat(first.mentorType().id()).isEqualTo(3L);
		assertThat(first.status()).isEqualTo(ReservationStatus.CONFIRMED);
	}

	@Test
	@DisplayName("단일_Reservation을_ReservationPageInfoDto로_변환_성공")
	void 단일_Reservation을_ReservationPageInfoDto로_변환_성공() {
		// Given
		Reservation reservation = createReservationWithDetails(1L);

		// When
		ReservationPageInfoDto result = ReservationConverter.toReservationPageInfoDto(reservation);

		// Then
		assertThat(result).isNotNull();
		assertThat(result.reservationId()).isEqualTo(1L);
		assertThat(result.scheduledDate()).isEqualTo(LocalDate.of(2026, 1, 30));
		assertThat(result.mentorType()).isNotNull();
		assertThat(result.mentorType().id()).isEqualTo(3L);
		assertThat(result.mentorType().name()).isEqualTo("스킨케어");
		assertThat(result.status()).isEqualTo(ReservationStatus.CONFIRMED);
	}

	// Helper Method
	private Reservation createReservationWithDetails(final Long reservationId) {
		User user = User.builder()
			.name("테스트 사용자")
			.email("user@test.com")
			.password("password")
			.kakaoId("kakao123")
			.build();
		ReflectionTestUtils.setField(user, "id", 1L);

		Mentor mentor = Mentor.builder()
			.name("테스트 멘토")
			.loginId("mentor_login")
			.password("password")
			.build();
		ReflectionTestUtils.setField(mentor, "id", 2L);

		MentorType mentorType = MentorType.builder()
			.typeName("스킨케어")
			.build();
		ReflectionTestUtils.setField(mentorType, "id", 3L);

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.of(2026, 1, 30))
			.scheduledTime(LocalTime.of(10, 0))
			.build();
		ReflectionTestUtils.setField(timetable, "id", 10L);

		TimetableSlot slot = TimetableSlot.builder()
			.timetable(timetable)
			.mentorType(mentorType)
			.build();
		ReflectionTestUtils.setField(slot, "id", 5L);

		Reservation reservation = Reservation.builder()
			.user(user)
			.mentor(mentor)
			.slot(slot)
			.status(ReservationStatus.CONFIRMED)
			.build();
		ReflectionTestUtils.setField(reservation, "id", reservationId);
		ReflectionTestUtils.setField(reservation, "createdAt", LocalDateTime.now());
		ReflectionTestUtils.setField(reservation, "updatedAt", LocalDateTime.now());

		return reservation;
	}
}
