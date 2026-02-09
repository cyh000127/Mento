package com.mento.domain.reservation.service.query;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.reservation.exception.ReservationException;
import com.mento.domain.reservation.repository.ReservationRepository;
import com.mento.domain.user.entity.Role;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationQueryServiceImpl implements ReservationQueryService {
	private final ReservationRepository reservationRepository;

	@Override
	public boolean existById(final Long id) {
		boolean exists = reservationRepository.existsById(id);
		log.info("[Reservation] 예약 존재 여부 조회 결과 - id: {}, exists: {}", id, exists);
		return exists;
	}

	@Override
	public Reservation findById(final Long id) {
		Reservation reservation = reservationRepository.findById(id)
			.orElseThrow(() -> new ReservationException(ErrorCode.RESERVATION_NOT_FOUND));
		log.info("[Reservation] 예약 조회 완료 - id: {}, reservation: {}", id, reservation);
		return reservation;
	}

	@Override
	public Reservation findWithDetailsById(final Long id) {
		Reservation reservation = reservationRepository.findWithDetailsById(id)
			.orElseThrow(() -> new ReservationException(ErrorCode.RESERVATION_NOT_FOUND));
		log.info("[Reservation] 예약 상세 조회 완료 - id: {}", id);
		return reservation;
	}

	@Override
	public Page<Reservation> findAllByRoleAndIdAndStatusWithPageable(
		final Long memberId,
		final Role role,
		final ReservationStatus status,
		final LocalDate startDate,
		final LocalDate endDate,
		final Pageable pageable
	) {
		Long userId = (role == Role.USER) ? memberId : null;
		Long mentorId = (role == Role.MENTOR) ? memberId : null;

		Page<Reservation> page = reservationRepository.findAllByCondition(
			userId, mentorId, startDate, endDate, status, pageable
		);

		log.info(
			"[Reservation] 예약 목록 조회 완료 {role: {}, memberId: {}, status: {}, dateRange: {}-{}, total: {}, page: {}}",
			role, memberId, status, startDate, endDate, page.getTotalElements(), page.getNumber()
		);
		return page;
	}

	@Override
	public boolean existsByUserIdAndSlotIdAndStatusIn(
		final Long userId,
		final Long reservationId,
		final List<ReservationStatus> reservationStatuses
	) {
		return reservationRepository.existsByUserIdAndSlotIdAndStatusIn(userId, reservationId, reservationStatuses);
	}

	@Override
	public List<Reservation> findAllByTimetableIds(final List<Long> timetableIds) {
		List<Reservation> reservations = reservationRepository.findAllBySlotTimetableIdIn(timetableIds);
		log.info("[Reservation] 시간표 ID 목록으로 예약 조회 완료 {size: {}}", reservations.size());
		return reservations;
	}

}
