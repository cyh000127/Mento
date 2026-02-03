package com.mento.domain.auth.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.dto.Token;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.auth.dto.request.MentorLoginReqDto;
import com.mento.domain.auth.service.command.MentorAuthCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class MentorAuthCommandController {

	private final MentorAuthCommandService mentorAuthCommandService;

	@Operation(summary = "멘토 로그인 (테스트/시연용)", description = "멘토 ID와 비밀번호로 로그인하여 토큰을 발급받습니다.")
	@PostMapping("/login/mentor")
	public ResponseEntity<BaseResponse<Token>> login(@Valid @RequestBody MentorLoginReqDto reqDto) {
		Token token = mentorAuthCommandService.login(reqDto);
		return ResponseUtils.ok(token);
	}
}
