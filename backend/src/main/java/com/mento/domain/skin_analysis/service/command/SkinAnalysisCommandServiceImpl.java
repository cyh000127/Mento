package com.mento.domain.skin_analysis.service.command;

import org.springframework.stereotype.Service;

import com.mento.common.config.restclient.SkinAnalysisRestClientContainer;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.skin_analysis.converter.SkinAnalysisConverter;
import com.mento.domain.skin_analysis.dto.request.SkinAnalysisReqDto;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisApiResWrapper;
import com.mento.domain.skin_analysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skin_analysis.entity.SkinAnalysis;
import com.mento.domain.skin_analysis.repository.SkinAnalysisRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysisCommandServiceImpl implements SkinAnalysisCommandService {

	private final SkinAnalysisRepository skinAnalysisRepository;
	private final SkinAnalysisRestClientContainer skinAnalysisRestClientContainer;

	@Override
	public SkinAnalysisDetailResDto analyzeSkin(Long userId, SkinAnalysisReqDto dto) {

		SkinAnalysisApiResWrapper wrapper = skinAnalysisRestClientContainer.get().post()
			.uri("/analyze")
			.body(dto)
			.retrieve()
			.body(SkinAnalysisApiResWrapper.class);

		if (wrapper.data() == null) {
			throw new BusinessException(ErrorCode.NOT_FOUND);
		}

		SkinAnalysisDetailResDto response = wrapper.data();

		SkinAnalysis entity = SkinAnalysisConverter.toEntity(userId, response);
		skinAnalysisRepository.save(entity);
		return response;
	}

}
