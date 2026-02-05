package com.mento.domain.consulting.service.query.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.consulting.exception.ConsultingException;
import com.mento.domain.consulting.repository.ConsultingRepository;
import com.mento.domain.consulting.service.query.ConsultingQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingQueryServiceImpl implements ConsultingQueryService {

	private final ConsultingRepository consultingRepository;

	@Override
	public Consulting findByRoomId(final String roomId) {
		Consulting consulting = consultingRepository.findByRoomId(roomId)
			.orElseThrow(() -> new ConsultingException(ErrorCode.CONSULTING_NOT_FOUND));
		log.info("Consulting 조회 성공 - roomId: {}, consultingId: {}", roomId, consulting.getId());
		return consulting;
	}
}
