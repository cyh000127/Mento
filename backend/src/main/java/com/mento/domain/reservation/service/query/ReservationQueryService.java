package com.mento.domain.reservation.service.query;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.user.entity.Role;

public interface ReservationQueryService {
	boolean existById(final Long id);

	Reservation findById(final Long id);

	Reservation findWithDetailsById(final Long id);

	Page<Reservation> findAllByRoleAndIdAndStatusWithPageable(
		final Long memberId,
		final Role role,
		final ReservationStatus status,
		final LocalDate startDate,
		final LocalDate endDate,
		final Pageable pageable
	);

	boolean existsByUserIdAndSlotIdAndStatusIn(Long userId, Long id, List<ReservationStatus> reservationStatuses);

	List<Reservation> findAllByTimetableIds(final List<Long> timetableIds);
}
