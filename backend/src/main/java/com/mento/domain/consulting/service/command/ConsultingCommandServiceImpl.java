package com.mento.domain.consulting.service.command;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.consulting.repository.ConsultingRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingCommandServiceImpl implements ConsultingCommandService {
	private final ConsultingRepository consultingRepository;

	public void saveDraftConsulting(final Consulting consulting) {
		consultingRepository.save(consulting);
		log.info("[Consulting] 초기 컨설팅 내역 정보 저장, {id: {}}", consulting.getId());
	}
}