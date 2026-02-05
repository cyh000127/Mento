package com.mento.domain.reservation.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.repository.ReservationRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationCommandServiceImpl implements ReservationCommandService {

	private final ReservationRepository reservationRepository;

	@Override
	public Reservation save(final Reservation reservation) {
		Reservation savedReservation = reservationRepository.save(reservation);
		log.info("[Reservation] 예약 정보 저장 완료 {id: {}, status: {}}", savedReservation.getId(),
			savedReservation.getStatus());
		return savedReservation;
	}

	@Override
	public void completeReservation(final Long reservationId) {
		Reservation reservation = reservationRepository.findById(reservationId)
			.orElseThrow(() -> new com.mento.common.error.exception.ReservationException(
				com.mento.common.error.ErrorCode.RESERVATION_NOT_FOUND));
		
		reservation.complete();
		log.info("[Reservation] 예약 상태 완료 변경 {id: {}}", reservationId);
	}
}

