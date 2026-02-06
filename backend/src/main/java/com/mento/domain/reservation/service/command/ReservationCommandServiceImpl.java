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
}

