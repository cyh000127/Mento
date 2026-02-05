package com.mento.domain.consulting.converter;

import com.mento.domain.consulting.dto.common.SummaryInfoDto;
import com.mento.domain.consulting.entity.ConsultingReport;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ConsultingReportConverter {

	public SummaryInfoDto toSummaryInfoDto(final ConsultingReport consultingReport) {
		return SummaryInfoDto.builder()
			.reportId(consultingReport.getId())
			.content(consultingReport.getContent())
			.mediaUrl(consultingReport.getMediaUrl())
			.build();
	}
}
