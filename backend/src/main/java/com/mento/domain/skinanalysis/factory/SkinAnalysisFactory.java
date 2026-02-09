package com.mento.domain.skinanalysis.factory;

import org.springframework.stereotype.Component;

import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysisFactory {

	public SkinAnalysis createSkinAnalysis(final SkinAnalysisDetailResDto aiResponse) {
		return SkinAnalysis.builder()
			.totalScore(aiResponse.totalScore())
			.totalGrade(aiResponse.totalGrade())
			.skinTypeSummary(aiResponse.skinTypeSummary())
			.analysisDetails(aiResponse.details())
			.build();
	}
}
