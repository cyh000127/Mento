package com.mento.domain.timetable.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mento.domain.timetable.entity.Timetable;

public interface TimetableRepository extends JpaRepository<Timetable, Long> {

	@Query("""
		SELECT t
		FROM Timetable t
		WHERE t.id = :timetableId
			AND t.deletedAt IS NULL
		""")
	Optional<Timetable> findByTimetableId(Long timetableId);

	@Query("""
		SELECT DISTINCT t.scheduledDate
		FROM Timetable t
		WHERE t.scheduledDate BETWEEN :startDate AND :endDate
			AND t.deletedAt IS NULL
		ORDER BY t.scheduledDate
		""")
	List<LocalDate> findDistinctDatesBetween(LocalDate startDate, LocalDate endDate);

	@Query("""
		SELECT t
		FROM Timetable t
		WHERE t.scheduledDate < :scheduledDateBefore
			AND t.deletedAt IS NULL
		""")
	List<Timetable> findAllByScheduledDateBefore(LocalDate scheduledDateBefore);

	@Query("""
		SELECT t
		FROM Timetable t
		WHERE t.scheduledDate BETWEEN :startDate AND :endDate
			AND t.deletedAt IS NULL
		ORDER BY t.scheduledDate, t.scheduledTime
		""")
	List<Timetable> findAllByScheduledDateBetween(LocalDate startDate, LocalDate endDate);

	List<Timetable> findByScheduledDateAndScheduledTime(LocalDate scheduledDate, LocalTime scheduledTime);
}
