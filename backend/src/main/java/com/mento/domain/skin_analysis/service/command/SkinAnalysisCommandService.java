package com.mento.domain.skin_analysis.service.command;

import com.mento.domain.skin_analysis.dto.request.SkinAnalysisReqDto;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisDetailResDto;

public interface SkinAnalysisCommandService {
	SkinAnalysisDetailResDto analyzeSkin(Long userId, SkinAnalysisReqDto dto);
}
