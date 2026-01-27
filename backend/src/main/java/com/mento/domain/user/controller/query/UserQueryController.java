package com.mento.domain.user.controller.query;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.service.UserFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "회원 관리 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserQueryController {

	private final UserFacadeService userFacadeService;

	@Operation(summary = "회원 조회", description = "ID로 회원을 조회합니다.")
	@PreAuthorize("hasAnyAuthority('MENTO', 'ADMIN')")
	@GetMapping("/users/{id}")
	public ResponseEntity<BaseResponse<UserResDto>> getUser(
		@PathVariable final Long id
	) {
		UserResDto response = userFacadeService.getUser(id);
		return ResponseUtils.ok(response);
	}
}
