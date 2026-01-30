package com.mento.domain.reservation.service.command;

import com.mento.domain.reservation.entity.Reservation;

public interface ReservationCommandService {

	Reservation save(Reservation reservation);
}
