package com.mento.domain.consulting.service.query.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.error.ErrorCode;
import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.consulting.exception.ConsultingException;
import com.mento.domain.consulting.repository.ConsultingReportRepository;
import com.mento.domain.consulting.service.query.ConsultingReportQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingReportQueryServiceImpl implements ConsultingReportQueryService {

	private final ConsultingReportRepository consultingRepository;

	@Override
	public ConsultingReport findById(final Long id) {
		ConsultingReport consultingReport = consultingRepository.findById(id)
			.orElseThrow(() -> new ConsultingException(ErrorCode.CONSULTING_REPORT_NOT_FOUND));
		log.info("[ConsultingReport] 상담 요약본 조회 성공 - id: {}", consultingReport.getId());
		return consultingReport;
	}
}