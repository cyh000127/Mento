package com.mento.domain.reservation.repository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@EntityGraph(attributePaths = {"user", "mentor", "slot", "slot.timetable", "slot.mentorType"})
	Optional<Reservation> findWithDetailsById(Long id);

	@EntityGraph(attributePaths = {"slot", "slot.timetable", "slot.mentorType", "consultingReport"})
	@Query("""
		SELECT r
		FROM Reservation r
		WHERE (:userId IS NULL OR r.user.id = :userId)
			AND (:mentorId IS NULL OR r.mentor.id = :mentorId)
			AND (:startDate IS NULL OR r.slot.timetable.scheduledDate >= :startDate)
			AND (:endDate IS NULL OR r.slot.timetable.scheduledDate <= :endDate)
			AND (:status IS NULL OR r.status = :status)
		ORDER BY r.id DESC
		""")
	Page<Reservation> findAllByCondition(
		@Param("userId") Long userId,
		@Param("mentorId") Long mentorId,
		@Param("startDate") LocalDate startDate,
		@Param("endDate") LocalDate endDate,
		@Param("status") ReservationStatus status,
		Pageable pageable
	);

	boolean existsByUserIdAndSlotIdAndStatusIn(Long userId, Long slotId, Collection<ReservationStatus> statuses);

	Optional<Reservation> findBySlotTimetableId(Long timetableId);

	List<Reservation> findAllBySlotTimetableIdIn(List<Long> timetableIds);


}
