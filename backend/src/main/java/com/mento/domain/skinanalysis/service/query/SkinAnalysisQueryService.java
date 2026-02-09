package com.mento.domain.skinanalysis.service.query;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;

public interface SkinAnalysisQueryService {
	SkinAnalysis findById(Long id, AuthenticatedUser authUser);

	Page<SkinAnalysis> findAllByUserId(Long userId, Pageable pageable);
}
