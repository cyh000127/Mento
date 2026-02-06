package com.mento.domain.consulting.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.consulting.dto.request.ConsultingChatLogSaveReqDto;
import com.mento.domain.consulting.service.facade.ConsultingFacadeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Consulting", description = "컨설팅 관리 API")
@RestController
@RequestMapping("/api/v1/consulting")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingCommandController {

	private final ConsultingFacadeService facadeService;

	@PostMapping("/session/chat-log")
	public ResponseEntity<BaseResponse<Void>> saveChatLog(
		@Validated @RequestBody ConsultingChatLogSaveReqDto reqDto
	) {
		facadeService.saveChatLogToRedis(reqDto);
		return ResponseUtils.noContent();
	}
}
