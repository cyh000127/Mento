package com.mento.domain.skinanalysis.service.facade;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.config.restclient.SkinAnalysisRestClientContainer;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.skinanalysis.converter.SkinAnalysisConverter;
import com.mento.domain.skinanalysis.dto.request.SkinAnalysisAiReqDto;
import com.mento.domain.skinanalysis.dto.request.SkinAnalysisClientReqDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisApiResWrapper;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisDetailResDto;
import com.mento.domain.skinanalysis.dto.response.SkinAnalysisSummaryResDto;
import com.mento.domain.skinanalysis.entity.SkinAnalysis;
import com.mento.domain.skinanalysis.exception.SkinAnalysisException;
import com.mento.domain.skinanalysis.factory.SkinAnalysisFactory;
import com.mento.domain.skinanalysis.service.command.SkinAnalysisCommandService;
import com.mento.domain.skinanalysis.service.query.SkinAnalysisQueryService;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.command.UserCommandService;
import com.mento.domain.user.service.query.UserQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class SkinAnalysisFacadeService {

	private final SkinAnalysisQueryService skinAnalysisQueryService;
	private final SkinAnalysisCommandService skinAnalysisCommandService;
	private final SkinAnalysisRestClientContainer skinAnalysisRestClientContainer;
	private final SkinAnalysisFactory skinAnalysisFactory;

	private final UserQueryService userQueryService;
	private final UserCommandService userCommandService;

	@Transactional
	public SkinAnalysisDetailResDto analyzeSkin(final Long userId, final SkinAnalysisClientReqDto dto) {
		userCommandService.update(userId, new UserUpdateReqDto(dto.birthDate()));
		log.info("[SkinAnalysis] 사용자 생년월일 업데이트 완료 {userId: {}, birthDate: {}}",
			userId, dto.birthDate());

		SkinAnalysisAiReqDto aiRequest = SkinAnalysisConverter.toSkinAnalysisAiReqDto(dto);
		log.info("[SkinAnalysis] AI 분석 요청 시작 {userId: {}, age: {}, gender: {}}",
			userId, aiRequest.age(), aiRequest.gender());

		SkinAnalysisApiResWrapper wrapper = skinAnalysisRestClientContainer.get().post()
			.uri("/analyze")
			.body(aiRequest)
			.retrieve()
			.onStatus(HttpStatusCode::is4xxClientError, ((_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("[SkinAnalysis] AI 서버 요청 실패(4xx) {body: {}}", errorBody);
				throw new SkinAnalysisException(ErrorCode.EXTERNAL_API_ERROR);
			}))
			.onStatus(HttpStatusCode::is5xxServerError, (_, res) -> {
				String errorBody = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
				log.error("[SkinAnalysis] AI 서버 오류(5xx) {body: {}}", errorBody);
				throw new SkinAnalysisException(ErrorCode.EXTERNAL_SERVER_ERROR);
			})
			.body(SkinAnalysisApiResWrapper.class);

		SkinAnalysisDetailResDto aiResponse = Optional.ofNullable(wrapper)
			.map(SkinAnalysisApiResWrapper::data)
			.orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND));

		log.info("[SkinAnalysis] AI 분석 응답 수신 완료 {userId: {}, totalScore: {}, totalGrade: {}}",
			userId, aiResponse.totalScore(), aiResponse.totalGrade());

		SkinAnalysis skinAnalysis = skinAnalysisFactory.createSkinAnalysis(aiResponse);
		SkinAnalysis savedSkinAnalysis = skinAnalysisCommandService.save(skinAnalysis);

		User user = userQueryService.findById(userId);
		user.assignSkinAnalysis(savedSkinAnalysis);

		return SkinAnalysisConverter.toSkinAnalysisDetailResDto(savedSkinAnalysis);
	}

	public SkinAnalysisDetailResDto getById(final Long id, final AuthenticatedUser authUser) {
		SkinAnalysis entity = skinAnalysisQueryService.findById(id, authUser);
		return SkinAnalysisConverter.toSkinAnalysisDetailResDto(entity);
	}

	public Page<SkinAnalysisSummaryResDto> getMySummaries(final Long userId, final Pageable pageable) {
		Page<SkinAnalysis> entityPage = skinAnalysisQueryService.findAllByUserId(userId, pageable);
		return entityPage.map(SkinAnalysisConverter::toSkinAnalysisSummaryResDto);
	}
}
