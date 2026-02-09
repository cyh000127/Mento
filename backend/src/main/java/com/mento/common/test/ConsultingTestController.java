package com.mento.common.test;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.ai.service.AiService;
import com.mento.common.config.properties.PromptProperties;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test - Consulting", description = "컨설팅 AI 리포트 테스트 API")
@SecurityRequirements // 인증 불필요
@RestController
@RequestMapping("/test/v1/consulting")
@RequiredArgsConstructor
@EnableConfigurationProperties(PromptProperties.class)
public class ConsultingTestController {

	private final AiService<String> aiService;
	private final PromptProperties promptProperties;

	@Schema(description = "AI 컨설팅 리포트 테스트 요청 DTO")
	public record TestRequest(
		@Schema(description = "상담 카테고리", example = "스킨케어", allowableValues = {"스킨케어", "메이크업", "헤어"})
		String category,

		@Schema(description = "STT 변환된 상담 텍스트", example = "오늘 상담은 피부 건조함에 대해 이야기했습니다...")
		String text
	) {
	}

	@Schema(description = "AI 컨설팅 리포트 테스트 응답 DTO")
	public record TestResponse(
		@Schema(description = "상담 카테고리", example = "스킨케어")
		String category,

		@Schema(description = "AI가 생성한 컨설팅 리포트 JSON", example = "{\"category\":\"스킨케어\",\"sessions\":[...]}")
		String aiResult
	) {
	}

	@Operation(
		summary = "AI 컨설팅 리포트 생성 테스트",
		description = "STT 텍스트와 카테고리를 입력받아 AI가 생성한 컨설팅 리포트를 반환합니다. "
			+ "카테고리는 스킨케어, 메이크업, 헤어 중 선택 가능합니다."
	)
	@PostMapping("/reports")
	public ResponseEntity<BaseResponse<TestResponse>> generateConsultingReportTest(
		@RequestBody TestRequest request
	) {
		PromptTemplate promptTemplate = new PromptTemplate(promptProperties.consulting());
		promptTemplate.add("category", request.category());
		promptTemplate.add("sttText", request.text());
		BeanOutputConverter<String> converter = new BeanOutputConverter<>(String.class);
		String aiResult = aiService.execute(promptProperties.system(), promptTemplate, converter);
		return ResponseUtils.ok(new TestResponse(request.category(), aiResult));
	}
}
