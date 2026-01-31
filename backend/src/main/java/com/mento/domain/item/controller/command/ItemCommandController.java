package com.mento.domain.item.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.request.UserItemAddReqDto;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.service.facade.ItemFacadeService;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Item", description = "유저 아이템 관리 API")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCommandController {

	private final ItemFacadeService itemFacadeService;

	@PostMapping
	public ResponseEntity<BaseResponse<ItemInfoResDto>> addItem(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@Validated @RequestBody final UserItemAddReqDto reqDto
	) {
		ItemInfoResDto response = itemFacadeService.addUserItem(authUser.getId(), reqDto);
		return ResponseUtils.ok(response);
	}

	@PutMapping("/{id}")
	public ResponseEntity<BaseResponse<ItemInfoResDto>> updateItemStatus(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id,
		@RequestParam final ItemStatus itemStatus
	) {
		ItemInfoResDto response = itemFacadeService.updateStatus(authUser.getId(), id, itemStatus);
		return ResponseUtils.ok(response);
	}

}
