package com.mento.domain.skinanalysis.service.command;

import com.mento.domain.skinanalysis.dto.request.SkinAnalysisReqDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;

public interface SkinAnalysisCommandService {
	SkinAnalysisDetailResDto analyze(Long userId, SkinAnalysisReqDto dto);
}
