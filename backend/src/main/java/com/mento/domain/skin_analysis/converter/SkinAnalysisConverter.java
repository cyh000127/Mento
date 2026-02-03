package com.mento.domain.skin_analysis.converter;

import com.mento.domain.skin_analysis.dto.response.SkinAnalysisResDto;
import com.mento.domain.skin_analysis.entity.SkinAnalysis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SkinAnalysisConverter {

	public SkinAnalysis toEntity(Long userId, SkinAnalysisResDto dto) {
		return SkinAnalysis.builder()
			.userId(userId)
			.analysisResult(dto)
			.build();
	}
}
