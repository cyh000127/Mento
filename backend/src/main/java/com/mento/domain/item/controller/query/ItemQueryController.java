package com.mento.domain.item.controller.query;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.response.PageResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.item.dto.request.ItemHistoryReqDto;
import com.mento.domain.item.dto.response.ItemHistoryResDto;
import com.mento.domain.item.dto.response.ItemInfoDetailResDto;
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.item.enums.ItemCategory;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.enums.SortType;
import com.mento.domain.item.service.facade.ItemFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "User Item", description = "유저 아이템 관리 API")
@RestController
@RequestMapping("/api/v1/items")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemQueryController {

	private final ItemFacadeService itemFacadeService;

	@Operation(
		summary = "아이템 목록 조회",
		description = "사용자의 아이템 목록을 조회합니다. "
			+ "상태(OWNED/USED/EMPTIED), 카테고리(HAIR/SKIN/BODY 등), 즐겨찾기 여부로 필터링할 수 있으며, "
			+ "정렬 방식(LATEST/OLDEST/NAME)을 지정할 수 있습니다. 페이지네이션을 지원합니다."
	)
	@GetMapping
	public ResponseEntity<PageResponse<ItemPageResDto>> findAllItemsByUserId(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@RequestParam(required = false)
		@Schema(description = "아이템 상태 필터링 (선택)", example = "OWNED") final ItemStatus status,
		@RequestParam(required = false)
		@Schema(description = "아이템 카테고리 (선택)", example = "HAIR") final ItemCategory category,
		@RequestParam(required = false)
		@Schema(description = "즐겨찾기 필터링 (선택)", example = "true") final Boolean isFavorite,
		@RequestParam(defaultValue = "LATEST")
		@Schema(description = "정렬 방식", example = "LATEST") final SortType sort,
		@RequestParam(defaultValue = "0")
		@Schema(description = "페이지 번호 (0부터 시작)", example = "0") final int page,
		@RequestParam(defaultValue = "10")
		@Schema(description = "페이지 크기", example = "20") final int size
	) {
		Page<ItemPageResDto> response = itemFacadeService.findAllItemsByUserId(
			authUser.getId(), status, category, isFavorite, sort, page, size
		);
		return ResponseUtils.page(response);
	}

	@Operation(
		summary = "아이템 상세 조회",
		description = "아이템의 상세 정보를 조회합니다. "
			+ "아이템 ID를 통해 브랜드, 카테고리, 상태, 즐겨찾기 여부 등의 상세 정보를 확인할 수 있습니다."
	)
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<ItemInfoDetailResDto>> findItemById(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@PathVariable final Long id
	) {
		ItemInfoDetailResDto response = itemFacadeService.findById(authUser.getId(), id);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "아이템 변경 이력 조회",
		description = "사용자의 아이템 변경 이력을 조회합니다. "
			+ "아이템의 상태 변경, 추가, 삭제 등의 이력을 시간순으로 확인할 수 있습니다. 페이지네이션을 지원합니다."
	)
	@GetMapping("/histories")
	public ResponseEntity<PageResponse<ItemHistoryResDto>> getItemHistories(
		@AuthenticationPrincipal final AuthenticatedUser authUser,
		@Validated @ModelAttribute final ItemHistoryReqDto reqDto
	) {
		Page<ItemHistoryResDto> response = itemFacadeService.getItemHistories(authUser.getId(), reqDto);
		return ResponseUtils.page(response);
	}
}
