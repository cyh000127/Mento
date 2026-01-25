package com.mready.domain.member.controller.query;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mready.common.response.BaseResponse;
import com.mready.common.util.ResponseUtils;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.service.MemberFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberQueryController {

	private final MemberFacadeService memberFacadeService;

	@Operation(summary = "회원 조회", description = "ID로 회원을 조회합니다.")
	@PreAuthorize("hasAnyAuthority('MENTO', 'ADMIN')")
	@GetMapping("/members/{id}")
	public ResponseEntity<BaseResponse<MemberResDto>> getMember(
			@PathVariable final Long id) {
		MemberResDto response = memberFacadeService.getMember(id);
		return ResponseUtils.ok(response);
	}
}
