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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Item", description = "유저 아이템 관리 API")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemCommandController {

	private final ItemFacadeService itemFacadeService;

	@Operation(
		summary = "아이템 추가",
		description = "사용자의 인벤토리에 새로운 아이템을 추가합니다. "
			+ "아이템 정보(브랜드, 카테고리, 이름 등)를 제공하면 사용자의 아이템 목록에 추가됩니다."
	)
	@PostMapping
	public ResponseEntity<BaseResponse<ItemInfoResDto>> addItem(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@Validated @RequestBody final UserItemAddReqDto reqDto
	) {
		ItemInfoResDto response = itemFacadeService.addUserItem(authUser.getId(), reqDto);
		return ResponseUtils.created(response);
	}

	@Operation(
		summary = "아이템 상태 변경",
		description = "아이템의 상태를 변경합니다 (OWNED, USED, EMPTIED). "
			+ "아이템 사용 여부 및 소진 상태를 관리할 수 있습니다."
	)
	@PutMapping("/{id}")
	public ResponseEntity<BaseResponse<ItemInfoResDto>> updateItemStatus(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id,
		@RequestParam final ItemStatus itemStatus
	) {
		ItemInfoResDto response = itemFacadeService.updateStatus(authUser.getId(), id, itemStatus);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "즐겨찾기 토글",
		description = "아이템의 즐겨찾기 상태를 토글합니다. "
			+ "즐겨찾기로 설정된 아이템은 목록 조회 시 필터링할 수 있습니다."
	)
	@PostMapping("/{id}/favorite")
	public ResponseEntity<BaseResponse<ItemInfoResDto>> toggleFavorite(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id
	) {
		ItemInfoResDto response = itemFacadeService.toggleFavorite(authUser.getId(), id);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "아이템 삭제",
		description = "아이템을 소프트 삭제합니다 (deletedAt 설정). "
			+ "삭제된 아이템은 목록 조회 시 표시되지 않으며, 복구가 가능합니다."
	)
	@PostMapping("/{id}")
	public ResponseEntity<BaseResponse<Void>> softDeleteItem(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id
	) {
		itemFacadeService.deleteItem(authUser.getId(), id);
		return ResponseUtils.noContent();
	}
}
