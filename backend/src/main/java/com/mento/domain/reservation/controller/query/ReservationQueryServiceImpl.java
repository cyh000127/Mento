package com.mento.domain.reservation.controller.query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
		log.debug("예약 존재 여부 조회 결과 - id: {}, exists: {}", id, exists);
		return exists;
	}

}
