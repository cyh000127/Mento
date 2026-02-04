package com.mento.domain.consulting.service.command.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.consulting.repository.ConsultingReportRepository;
import com.mento.domain.consulting.service.command.ConsultingReportCommandService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingReportCommandServiceImpl implements ConsultingReportCommandService {
	private final ConsultingReportRepository reportRepository;

	@Override
	@Transactional
	public ConsultingReport save(final ConsultingReport consultingReport) {
		ConsultingReport savedConsultingReport = reportRepository.save(consultingReport);
		log.info("[ConsultingReport] 컨설팅 요약본 저장 완료, {id: {}}", savedConsultingReport.getId());
		return savedConsultingReport;
	}
}