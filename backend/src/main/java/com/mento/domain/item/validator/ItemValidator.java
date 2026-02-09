package com.mento.domain.item.validator;

import java.util.Objects;

import org.springframework.stereotype.Component;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.exception.ItemException;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.user.entity.Role;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class ItemValidator {

	public void validate(final Long userId, final Item item) {
		validateItemBelongsToUser(userId, item);
	}

	private void validateItemBelongsToUser(final Long userId, final Item item) {
		if (!Objects.equals(userId, item.getUser().getId())) {
			throw new ItemException(ErrorCode.ITEM_ACCESS_DENIED);
		}
	}

	public void validateMentorAccess(
		final AuthenticatedUser authUser,
		final Reservation reservation,
		final Long userId
	) {
		validateMentorRole(authUser);
		validateMentorOwnsReservation(authUser.getId(), reservation);
		validateReservationMatchesUser(userId, reservation);
	}

	private void validateMentorRole(final AuthenticatedUser authUser) {
		if (Role.fromString(authUser.getRole()) != Role.MENTOR) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}
	}

	private void validateMentorOwnsReservation(final Long mentorId, final Reservation reservation) {
		if (!isMentorOfReservation(mentorId, reservation)) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}
	}

	private void validateReservationMatchesUser(final Long userId, final Reservation reservation) {
		if (!isUserOfReservation(userId, reservation)) {
			throw new BusinessException(ErrorCode.ACCESS_DENIED);
		}
	}

	private boolean isMentorOfReservation(final Long mentorId, final Reservation reservation) {
		return reservation.getMentor() != null
			&& mentorId.equals(reservation.getMentor().getId());
	}

	private boolean isUserOfReservation(final Long userId, final Reservation reservation) {
		return reservation.getUser() != null
			&& userId.equals(reservation.getUser().getId());
	}

}
