package com.mento.domain.reservation.service.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.exception.ReservationException;
import com.mento.domain.reservation.repository.ReservationRepository;

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

}
