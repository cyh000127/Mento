package com.mento.domain.skin_analysis.service.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mento.domain.skin_analysis.dto.request.SkinAnalysisReqDto;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skin_analysis.service.command.SkinAnalysisCommandService;
import com.mento.domain.skin_analysis.service.query.SkinAnalysisQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkinAnalysisFacadeService {

	private final SkinAnalysisQueryService skinAnalysisQueryService;
	private final SkinAnalysisCommandService skinAnalysisCommandService;

	public SkinAnalysisDetailResDto analyzeSkin(Long userId, SkinAnalysisReqDto dto) {
		return skinAnalysisCommandService.analyzeSkin(userId, dto);
	}

	public SkinAnalysisDetailResDto getById(Long id) {
		return skinAnalysisQueryService.getById(id);
	}

	public Page<SkinAnalysisSummaryResDto> getMySummaries(Long userId, Pageable pageable) {
		return skinAnalysisQueryService.getMySummaries(userId, pageable);
	}
}
