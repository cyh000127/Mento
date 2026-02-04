package com.mento.domain.skinanalysis.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisSummaryResDto;

public interface SkinAnalysisQueryService {
	SkinAnalysisDetailResDto getById(Long id, AuthenticatedUser authUser);

	Page<SkinAnalysisSummaryResDto> getMySummaries(Long userId, Pageable pageable);
}
