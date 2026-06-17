package com.mento.domain.reservation.repository;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.mento.common.util.AesUtils;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnitUtil;

@DataJpaTest(properties = {
	"encryption.secret-key=0000000000000000000000000000000000000000000000000000000000000000",
	"spring.jpa.properties.hibernate.generate_statistics=true"
})
@ActiveProfiles("test")
@Import(AesUtils.class)
class ReservationRepositoryTest {

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	@DisplayName("시간표 ID로 예약 조회 시 알림 스케줄러에 필요한 연관 객체를 함께 로딩한다")
	void findAllBySlotTimetableIdIn_FetchesScheduleRelations() {
		// given
		Long timetableId = persistReservations(3);
		entityManager.flush();
		entityManager.clear();

		Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		statistics.clear();

		// when
		List<Reservation> reservations = reservationRepository.findAllBySlotTimetableIdIn(List.of(timetableId));
		long queryCountAfterFind = statistics.getPrepareStatementCount();

		// then
		assertThat(reservations).hasSize(3);
		assertThat(queryCountAfterFind).isEqualTo(1L);

		PersistenceUnitUtil persistenceUnitUtil = entityManagerFactory.getPersistenceUnitUtil();
		reservations.forEach(reservation -> {
			assertThat(persistenceUnitUtil.isLoaded(reservation, "user")).isTrue();
			assertThat(persistenceUnitUtil.isLoaded(reservation, "slot")).isTrue();
			assertThat(persistenceUnitUtil.isLoaded(reservation.getSlot(), "timetable")).isTrue();
		});

		reservations.forEach(reservation -> {
			assertThat(reservation.getUser().getId()).isNotNull();
			assertThat(reservation.getSlot().getTimetable().getScheduledTime()).isEqualTo(LocalTime.of(10, 0));
		});
		assertThat(statistics.getPrepareStatementCount()).isEqualTo(queryCountAfterFind);
	}

	private Long persistReservations(final int reservationCount) {
		MentorType mentorType = MentorType.builder()
			.typeName("스킨케어")
			.price(35000)
			.build();
		entityManager.persist(mentorType);

		Timetable timetable = Timetable.builder()
			.scheduledDate(LocalDate.of(2026, 1, 30))
			.scheduledTime(LocalTime.of(10, 0))
			.build();
		entityManager.persist(timetable);

		TimetableSlot slot = TimetableSlot.builder()
			.timetable(timetable)
			.mentorType(mentorType)
			.build();
		entityManager.persist(slot);

		for (int index = 0; index < reservationCount; index++) {
			User user = User.builder()
				.name("schedule-user-" + index)
				.email("schedule-user-" + index + "@test.com")
				.password("password")
				.kakaoId("kakao-" + index)
				.build();
			entityManager.persist(user);

			entityManager.persist(Reservation.builder()
				.user(user)
				.slot(slot)
				.status(ReservationStatus.CONFIRMED)
				.build());
		}

		return timetable.getId();
	}
}
