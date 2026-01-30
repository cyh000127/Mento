package com.mento.domain.reservation.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.mento.domain.reservation.entity.Reservation;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

	@EntityGraph(attributePaths = {"user", "mentor", "slot", "slot.timetable", "slot.mentorType"})
	Optional<Reservation> findWithDetailsById(Long id);
}
