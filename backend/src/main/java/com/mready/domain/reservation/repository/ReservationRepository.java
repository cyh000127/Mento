package com.mready.domain.reservation.repository;

import com.mready.domain.reservation.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByTimetableId(Long timetableId);
}
