package com.mento.domain.skinanalysis.service.command;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;

import com.mento.common.config.restclient.SkinAnalysisRestClientContainer;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.skinanalysis.converter.SkinAnalysisConverter;
import com.mento.domain.skinanalysis.dto.request.SkinAnalysisAiReqDto;
import com.mento.domain.skinanalysis.dto.request.SkinAnalysisClientReqDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisApiResWrapper;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;
import com.mento.domain.skinanalysis.repository.SkinAnalysisRepository;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.service.command.UserCommandService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysisCommandServiceImpl implements SkinAnalysisCommandService {

	private final SkinAnalysisRepository skinAnalysisRepository;
	private final SkinAnalysisRestClientContainer skinAnalysisRestClientContainer;
	private final UserCommandService userCommandService;

	@Override
	public SkinAnalysisDetailResDto analyze(Long userId, SkinAnalysisClientReqDto dto) {
		userCommandService.update(userId, new UserUpdateReqDto(dto.birthDate()));

		SkinAnalysisAiReqDto request = SkinAnalysisConverter.toSkinAnalysisAiReqDto(dto);

		SkinAnalysisApiResWrapper wrapper = skinAnalysisRestClientContainer.get().post()
			.uri("/analyze")
			.body(request)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, ((_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("[SkinAnalysis] AI 서버 요청 실패(4xx) {body: {}}", errorBody);
			}))
			.onStatus(HttpStatusCode::is5xxServerError, (_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("[SkinAnalysis] AI 서버 오류(5xx) {body: {}}", errorBody);
			})
			.body(SkinAnalysisApiResWrapper.class);

		SkinAnalysisDetailResDto response = Optional.ofNullable(wrapper)
			.map(SkinAnalysisApiResWrapper::data)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		SkinAnalysis entity = SkinAnalysisConverter.toEntity(userId, response);
		skinAnalysisRepository.save(entity);
		return response;
	}

}
