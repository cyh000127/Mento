package com.mento.domain.user.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.auth.service.command.AuthCommandService;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.user.dto.request.MentorAddItemReqDto;
import com.mento.domain.user.dto.request.UserUpdateReqDto;
import com.mento.domain.user.dto.response.UserResDto;
import com.mento.domain.user.service.UserFacadeService;
import com.mento.domain.user.service.command.UserCommandService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "User", description = "회원 관리 API")
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCommandController {

	private final UserCommandService userCommandService;
	private final UserFacadeService userFacadeService;
	private final AuthCommandService authCommandService;

	@Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 수행합니다 (Soft Delete).")
	@DeleteMapping("/account")
	public ResponseEntity<BaseResponse<Void>> withdraw(
		@AuthenticationPrincipal AuthenticatedUser authuser,
		HttpServletRequest request,
		HttpServletResponse response
	) {
		userCommandService.withdraw(authuser.getId());
		authCommandService.logout(request, response);
		return ResponseUtils.ok(null);
	}

	@Operation(summary = "회원 생일 수정", description = "회원 생일 정보를 수정합니다.")
	@PatchMapping("/edit")
	public ResponseEntity<BaseResponse<UserResDto>> updateUser(
		@AuthenticationPrincipal AuthenticatedUser authuser,
		@RequestBody @Valid UserUpdateReqDto reqDto
	) {
		UserResDto response = userFacadeService.updateUser(authuser, reqDto);
		return ResponseUtils.ok(response);
	}

	@Operation(summary = "고객 아이템 추가", description = "멘토가 예약한 고객의 인벤토리에 아이템을 추가합니다.")
	@PostMapping("/{userId}/items")
	public ResponseEntity<BaseResponse<ItemInfoResDto>> addItemToUser(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long userId,
		@Validated @RequestBody final MentorAddItemReqDto reqDto
	) {
		ItemInfoResDto response = userFacadeService.addItemToUser(authUser, userId, reqDto);
		return ResponseUtils.created(response);
	}
}
