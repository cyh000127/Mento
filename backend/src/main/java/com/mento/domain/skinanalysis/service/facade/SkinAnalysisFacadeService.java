package com.mento.domain.skinanalysis.service.facade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mento.domain.skinanalysis.dto.request.SkinAnalysisClientReqDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skinanalysis.service.command.SkinAnalysisCommandService;
import com.mento.domain.skinanalysis.service.query.SkinAnalysisQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkinAnalysisFacadeService {

	private final SkinAnalysisQueryService skinAnalysisQueryService;
	private final SkinAnalysisCommandService skinAnalysisCommandService;

	public SkinAnalysisDetailResDto analyzeSkin(Long userId, SkinAnalysisClientReqDto dto) {
		return skinAnalysisCommandService.analyze(userId, dto);
	}

	public SkinAnalysisDetailResDto getById(Long id) {
		return skinAnalysisQueryService.getById(id);
	}

	public Page<SkinAnalysisSummaryResDto> getMySummaries(Long userId, Pageable pageable) {
		return skinAnalysisQueryService.getMySummaries(userId, pageable);
	}
}
