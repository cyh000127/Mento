package com.mento.domain.timetable.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.mento.domain.timetable.entity.TimetableSlot;

public interface TimetableSlotRepository extends JpaRepository<TimetableSlot, Long> {

	@Query("""
		SELECT ts
		FROM TimetableSlot ts
		WHERE ts.timetable.id = :timetableId
			AND ts.mentorType.id = :typeId
			AND ts.deletedAt IS NULL
		""")
	Optional<TimetableSlot> findByTimetableIdAndTypeId(final Long timetableId, final Long typeId);

	@Query("""
		SELECT ts
		FROM TimetableSlot ts
		WHERE ts.timetable.id = :timetableId
			AND ts.deletedAt IS NULL
		ORDER BY ts.mentorType.id
		""")
	List<TimetableSlot> findAllByTimetableId(final Long timetableId);

	@Query("""
		SELECT ts
		FROM TimetableSlot ts
		WHERE ts.timetable.id IN :timetableIds
			AND ts.mentorType.id = :typeId
			AND ts.deletedAt IS NULL
		ORDER BY ts.timetable.scheduledDate, ts.timetable.scheduledTime
		""")
	List<TimetableSlot> findAllByTimetableIdsAndTypeId(final List<Long> timetableIds, final Long typeId);

	@Query("""
		SELECT ts
		FROM TimetableSlot ts
		WHERE ts.timetable.id IN :timetableIds
		ORDER BY ts.timetable.scheduledDate, ts.timetable.scheduledTime
		""")
	List<TimetableSlot> findAllByTimetableIds(List<Long> timetableIds);
}
