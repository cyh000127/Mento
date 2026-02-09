package com.mento.domain.auth.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.dto.Token;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.auth.dto.request.TestLoginReqDto;
import com.mento.domain.auth.service.command.TestAuthCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/test/auth")
@RequiredArgsConstructor
public class TestAuthCommandController {

	private final TestAuthCommandService testAuthCommandService;

	@Operation(summary = "테스트용 통합 로그인", description = "계정 정보로 로그인하여 24시간 유효한 토큰을 발급받습니다.")
	@PostMapping("/login")
	public ResponseEntity<BaseResponse<Token>> login(
		@Valid @RequestBody TestLoginReqDto reqDto,
		HttpServletResponse response
	) {
		Token token = testAuthCommandService.login(reqDto, response);
		return ResponseUtils.ok(token);
	}
}
