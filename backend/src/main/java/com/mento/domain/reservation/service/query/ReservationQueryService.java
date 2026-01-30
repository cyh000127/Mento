package com.mento.domain.reservation.service.query;

import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.entity.ReservationStatus;

public interface ReservationQueryService {
	boolean existById(final Long id);

	Reservation findById(final Long id);

	Reservation findWithDetailsById(final Long id);

	Page<Reservation> findAllByUserIdAndStatusWithPageable(
		final Long userId,
		final ReservationStatus status,
		final LocalDate startDate,
		final LocalDate endDate,
		final Pageable pageable
	);
}
