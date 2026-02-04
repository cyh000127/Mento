package com.mento.domain.skin_analysis.converter;

import com.mento.domain.skin_analysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skin_analysis.entity.SkinAnalysis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SkinAnalysisConverter {

	public SkinAnalysis toEntity(Long userId, SkinAnalysisDetailResDto dto) {
		return SkinAnalysis.builder()
			.userId(userId)
			.totalScore(dto.totalScore())
			.totalGrade(dto.totalGrade())
			.skinTypeSummary(dto.skinTypeSummary())
			.analysisDetails(dto.details())
			.build();
	}

	public SkinAnalysisDetailResDto toSkinAnalysisDetailResDto(SkinAnalysis entity) {
		return SkinAnalysisDetailResDto.builder()
			.totalScore(entity.getTotalScore())
			.totalGrade(entity.getTotalGrade())
			.skinTypeSummary(entity.getSkinTypeSummary())
			.details(entity.getAnalysisDetails())
			.build();
	}

}
