package com.mento.domain.reservation.factory;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.user.entity.User;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ReservationFactory {

	private static final int DRAFT_RESERVATION_TTL_MINUTES = 15;

	public Reservation createReservation(final User user, final TimetableSlot timetableSlot) {
		Reservation reservation = Reservation.builder()
			.status(ReservationStatus.IN_PROGRESS)
			.expiresAt(LocalDateTime.now().plusMinutes(DRAFT_RESERVATION_TTL_MINUTES))
			.build();

		reservation.assignUser(user);
		reservation.assignSlot(timetableSlot);

		return reservation;
	}
}
