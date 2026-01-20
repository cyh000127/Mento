package com.mready.domain.member.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mready.common.response.BaseResponse;
import com.mready.common.util.ResponseUtils;
import com.mready.domain.member.dto.request.MemberCreateReqDto;
import com.mready.domain.member.dto.response.MemberResDto;
import com.mready.domain.member.service.MemberFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Tag(name = "Member", description = "회원 관리 API")
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberCommandController {

	private final MemberFacadeService memberFacadeService;

	@Operation(summary = "회원 생성", description = "새로운 회원을 생성합니다.")
	@PostMapping
	public ResponseEntity<BaseResponse<MemberResDto>> createMember(
		@Valid @RequestBody final MemberCreateReqDto request
	) {
		MemberResDto response = memberFacadeService.createMember(request);
		return ResponseUtils.created(response);
	}
}
