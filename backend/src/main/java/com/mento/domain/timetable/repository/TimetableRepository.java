package com.mento.domain.timetable.repository;

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

}
