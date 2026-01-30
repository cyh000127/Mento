package com.mento.domain.reservation.validator;

import org.springframework.stereotype.Component;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.ReservationException;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.user.entity.Role;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationValidator {

	public void validateReservationAccess(final AuthenticatedUser authUser, final Reservation reservation) {
		Role role = Role.fromString(authUser.getRole());
		if (role == Role.MENTOR) {
			validateReservationBelongsToMentor(authUser.getId(), reservation);
		}
		if (role == Role.USER) {
			validateReservationBelongsToUser(authUser.getId(), reservation);
		}
	}

	private void validateReservationBelongsToMentor(final Long id, final Reservation reservation) {
		if (!reservation.getMentor().getId().equals(id)) {
			throw new ReservationException(ErrorCode.RESERVATION_ACCESS_DENIED);
		}
	}

	private void validateReservationBelongsToUser(final Long id, final Reservation reservation) {
		if (!reservation.getMentor().getId().equals(id)) {
			throw new ReservationException(ErrorCode.RESERVATION_ACCESS_DENIED);
		}
	}
}
