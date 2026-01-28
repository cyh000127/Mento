package com.mento.domain.timetable.repository;

import java.time.LocalDate;
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
		""")
	Optional<Timetable> findByTimetableId(Long timetableId);

	@Query("""
		SELECT DISTINCT t.scheduledDate
		FROM Timetable t
		WHERE t.scheduledDate BETWEEN :startDate AND :endDate
		ORDER BY t.scheduledDate
		""")
	List<LocalDate> findDistinctDatesBetween(LocalDate startDate, LocalDate endDate);

	List<Timetable> findAllByScheduledDateBefore(LocalDate scheduledDateBefore);

	@Query("""
		SELECT t
		FROM Timetable t
		WHERE t.scheduledDate BETWEEN :startDate AND :endDate
		ORDER BY t.scheduledDate, t.scheduledTime
		""")
	List<Timetable> findAllByScheduledDateBetween(LocalDate startDate, LocalDate endDate);
}
