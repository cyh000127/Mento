package com.mento.domain.skinanalysis.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisSummaryResDto;

public interface SkinAnalysisQueryService {
	SkinAnalysisDetailResDto getById(Long id);

	Page<SkinAnalysisSummaryResDto> getMySummaries(Long userId, Pageable pageable);
}
