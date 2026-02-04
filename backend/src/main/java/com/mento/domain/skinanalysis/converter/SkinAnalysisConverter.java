package com.mento.domain.skinanalysis.converter;

import com.mento.domain.skinanalysis.dto.request.SkinAnalysisAiReqDto;
import com.mento.domain.skinanalysis.dto.request.SkinAnalysisClientReqDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;

import lombok.experimental.UtilityClass;

@UtilityClass
public class SkinAnalysisConverter {

	public SkinAnalysisSummaryResDto toSkinAnalysisSummaryResDto(SkinAnalysis entity) {
		return SkinAnalysisSummaryResDto.builder()
			.id(entity.getId())
			.createdAt(entity.getCreatedAt())
			.totalScore(entity.getTotalScore())
			.skinTypeSummary(entity.getSkinTypeSummary())
			.build();
	}

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

	public SkinAnalysisAiReqDto toSkinAnalysisAiReqDto(SkinAnalysisClientReqDto dto) {
		return SkinAnalysisAiReqDto.builder()
			.frontUrl(dto.frontUrl())
			.l30Url(dto.l30Url())
			.r30Url(dto.r30Url())
			.age(dto.getCalculatedAge())
			.gender(dto.gender())
			.build();
	}

}
