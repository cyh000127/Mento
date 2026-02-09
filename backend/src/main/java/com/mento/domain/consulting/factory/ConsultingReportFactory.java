package com.mento.domain.consulting.factory;

import org.springframework.stereotype.Component;

import com.mento.domain.consulting.entity.ConsultingReport;

@Component
public class ConsultingReportFactory {

	public ConsultingReport createInitReport() {
		return ConsultingReport.builder()
			.build();
	}
}
