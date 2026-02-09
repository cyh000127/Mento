package com.mento.domain.consulting.service.command.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.consulting.repository.ConsultingReportRepository;
import com.mento.domain.consulting.service.command.ConsultingReportCommandService;
import com.mento.domain.consulting.service.query.ConsultingReportQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingReportCommandServiceImpl implements ConsultingReportCommandService {
	private final ConsultingReportRepository reportRepository;
	private final ConsultingReportQueryService consultingReportQueryService;

	@Override
	public ConsultingReport save(final ConsultingReport consultingReport) {
		ConsultingReport savedConsultingReport = reportRepository.save(consultingReport);
		log.info("[ConsultingReport] 컨설팅 요약본 저장 완료, {id: {}}", savedConsultingReport.getId());
		return savedConsultingReport;
	}

	@Override
	public void updateVideo(final Long reservationId, final String mediaUrl) {
		ConsultingReport consultingReport = consultingReportQueryService.findByReservationId(reservationId);
		consultingReport.updateVideo(mediaUrl);
		log.info("[ConsultingReport] 비디오 URL 업데이트 완료 {reservationId: {}, mediaUrl: {}}",
			reservationId, mediaUrl);
	}
}