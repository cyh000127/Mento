package com.mento.domain.notification.service.schedule;

import static org.assertj.core.api.Assertions.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.util.AesUtils;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.notification.converter.NotificationConverter;
import com.mento.domain.notification.entity.Notification;
import com.mento.domain.notification.entity.NotificationType;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.reservation.repository.ReservationRepository;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.User;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

@SpringBootTest(classes = NotificationScheduleReservationFetchBenchmarkTest.BenchmarkConfig.class, properties = {
	"encryption.secret-key=0000000000000000000000000000000000000000000000000000000000000000",
	"spring.ai.openai.api-key=test-key",
	"spring.ai.openai.speech.api-key=test-key",
	"spring.jpa.properties.hibernate.generate_statistics=true",
	"spring.task.scheduling.enabled=false"
})
@ActiveProfiles("test")
@Transactional
@EnabledIfEnvironmentVariable(named = "BENCHMARK", matches = "true")
class NotificationScheduleReservationFetchBenchmarkTest {

	private static final int RESERVATION_COUNT = 300;
	private static final int WARMUP_COUNT = 3;
	private static final int MEASURE_COUNT = 5;

	@Autowired
	private ReservationRepository reservationRepository;

	@Autowired
	private EntityManager entityManager;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	@DisplayName("예약_알림_스케줄러_예약_조회_성능을_비교한다")
	void 예약_알림_스케줄러_예약_조회_성능을_비교한다() {
		// given
		Timetable timetable = prepareReservations();
		List<Long> timetableIds = List.of(timetable.getId());
		Map<Long, Timetable> timetableMap = Map.of(timetable.getId(), timetable);

		for (int warmupIndex = 0; warmupIndex < WARMUP_COUNT; warmupIndex++) {
			measureLegacyReservationMapping(timetableIds, timetableMap);
			measureEntityGraphReservationMapping(timetableIds, timetableMap);
		}

		// when
		List<BenchmarkSample> legacySamples = new ArrayList<>();
		List<BenchmarkSample> entityGraphSamples = new ArrayList<>();
		for (int measureIndex = 0; measureIndex < MEASURE_COUNT; measureIndex++) {
			legacySamples.add(measureLegacyReservationMapping(timetableIds, timetableMap));
			entityGraphSamples.add(measureEntityGraphReservationMapping(timetableIds, timetableMap));
		}

		BenchmarkSummary legacySummary = BenchmarkSummary.from(legacySamples);
		BenchmarkSummary entityGraphSummary = BenchmarkSummary.from(entityGraphSamples);
		System.out.printf(
			"%n[NotificationScheduleReservationFetch][legacy] reservations=%d, avgMs=%.2f, p95Ms=%.2f, "
				+ "preparedStatements=%.1f, entityLoads=%.1f%n",
			RESERVATION_COUNT,
			legacySummary.averageElapsedMillis(),
			legacySummary.p95ElapsedMillis(),
			legacySummary.averagePreparedStatements(),
			legacySummary.averageEntityLoads()
		);
		System.out.printf(
			"[NotificationScheduleReservationFetch][entityGraph] reservations=%d, avgMs=%.2f, p95Ms=%.2f, "
				+ "preparedStatements=%.1f, entityLoads=%.1f%n",
			RESERVATION_COUNT,
			entityGraphSummary.averageElapsedMillis(),
			entityGraphSummary.p95ElapsedMillis(),
			entityGraphSummary.averagePreparedStatements(),
			entityGraphSummary.averageEntityLoads()
		);

		// then
		assertThat(legacySummary.averageNotificationCount()).isEqualTo(RESERVATION_COUNT);
		assertThat(entityGraphSummary.averageNotificationCount()).isEqualTo(RESERVATION_COUNT);
		assertThat(entityGraphSummary.averagePreparedStatements())
			.isLessThan(legacySummary.averagePreparedStatements());
	}

	private BenchmarkSample measureLegacyReservationMapping(
		final List<Long> timetableIds,
		final Map<Long, Timetable> timetableMap
	) {
		entityManager.flush();
		entityManager.clear();

		Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		statistics.clear();

		long startNanos = System.nanoTime();
		List<Reservation> reservations = entityManager.createQuery("""
				SELECT r
				FROM Reservation r
				WHERE r.slot.timetable.id IN :timetableIds
				""", Reservation.class)
			.setParameter("timetableIds", timetableIds)
			.getResultList();
		List<Notification> notifications = mapToNotifications(reservations, timetableMap);
		long elapsedNanos = System.nanoTime() - startNanos;

		return new BenchmarkSample(
			elapsedNanos,
			statistics.getPrepareStatementCount(),
			statistics.getEntityLoadCount(),
			notifications.size()
		);
	}

	private BenchmarkSample measureEntityGraphReservationMapping(
		final List<Long> timetableIds,
		final Map<Long, Timetable> timetableMap
	) {
		entityManager.flush();
		entityManager.clear();

		Statistics statistics = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		statistics.clear();

		long startNanos = System.nanoTime();
		List<Reservation> reservations = reservationRepository.findAllBySlotTimetableIdIn(timetableIds);
		List<Notification> notifications = mapToNotifications(reservations, timetableMap);
		long elapsedNanos = System.nanoTime() - startNanos;

		return new BenchmarkSample(
			elapsedNanos,
			statistics.getPrepareStatementCount(),
			statistics.getEntityLoadCount(),
			notifications.size()
		);
	}

	private List<Notification> mapToNotifications(
		final List<Reservation> reservations,
		final Map<Long, Timetable> timetableMap
	) {
		return reservations.stream()
			.map(reservation -> {
				Timetable timetable = timetableMap.get(reservation.getSlot().getTimetable().getId());
				LocalDateTime scheduledDateTime = LocalDateTime.of(
					timetable.getScheduledDate(),
					timetable.getScheduledTime()
				);

				return NotificationConverter.toEntity(
					reservation.getUser(),
					NotificationType.RESERVATION_REMINDER,
					"60",
					scheduledDateTime.minusMinutes(30)
				);
			})
			.toList();
	}

	private Timetable prepareReservations() {
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

		for (int index = 0; index < RESERVATION_COUNT; index++) {
			TimetableSlot slot = TimetableSlot.builder()
				.timetable(timetable)
				.mentorType(mentorType)
				.build();
			entityManager.persist(slot);
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
		entityManager.flush();
		entityManager.clear();

		return timetable;
	}

	private record BenchmarkSample(
		long elapsedNanos,
		long preparedStatements,
		long entityLoads,
		int notificationCount
	) {
		double elapsedMillis() {
			return elapsedNanos / 1_000_000.0;
		}
	}

	private record BenchmarkSummary(
		double averageElapsedMillis,
		double p95ElapsedMillis,
		double averagePreparedStatements,
		double averageEntityLoads,
		int averageNotificationCount
	) {
		private static BenchmarkSummary from(final List<BenchmarkSample> samples) {
			List<BenchmarkSample> sortedSamples = samples.stream()
				.sorted(Comparator.comparingDouble(BenchmarkSample::elapsedMillis))
				.toList();
			int p95Index = Math.min(sortedSamples.size() - 1, (int)Math.ceil(sortedSamples.size() * 0.95) - 1);

			return new BenchmarkSummary(
				samples.stream().mapToDouble(BenchmarkSample::elapsedMillis).average().orElse(0.0),
				sortedSamples.get(p95Index).elapsedMillis(),
				samples.stream().mapToLong(BenchmarkSample::preparedStatements).average().orElse(0.0),
				samples.stream().mapToLong(BenchmarkSample::entityLoads).average().orElse(0.0),
				(int)samples.stream().mapToInt(BenchmarkSample::notificationCount).average().orElse(0.0)
			);
		}
	}

	@SpringBootConfiguration
	@EnableAutoConfiguration
	@EnableJpaAuditing
	@EnableTransactionManagement
	@EntityScan(basePackages = "com.mento.domain")
	@EnableJpaRepositories(basePackageClasses = ReservationRepository.class)
	@Import(AesUtils.class)
	static class BenchmarkConfig {
	}
}
