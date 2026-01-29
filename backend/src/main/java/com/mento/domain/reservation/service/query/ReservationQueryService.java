package com.mento.domain.reservation.service.query;

import com.mento.domain.reservation.entity.Reservation;

public interface ReservationQueryService {
	boolean existById(final Long id);

	Reservation findById(final Long id);
}
