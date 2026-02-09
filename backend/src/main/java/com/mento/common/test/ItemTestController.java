package com.mento.common.test;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.response.PageResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.item.dto.common.ItemInfoResDto;
import com.mento.domain.item.dto.request.ItemHistoryReqDto;
import com.mento.domain.item.dto.request.ItemSearchReqDto;
import com.mento.domain.item.dto.request.UserItemAddReqDto;
import com.mento.domain.item.dto.response.ItemHistoryResDto;
import com.mento.domain.item.dto.response.ItemInfoDetailResDto;
import com.mento.domain.item.dto.response.ItemPageResDto;
import com.mento.domain.item.entity.Item;
import com.mento.domain.item.enums.ItemStatus;
import com.mento.domain.item.service.facade.ItemFacadeService;
import com.mento.domain.item.service.query.ItemQueryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test - Item", description = "아이템 테스트용 API (인증 없이 아이템 ID로 직접 접근)")
@SecurityRequirements // 인증 불필요
@RestController
@RequestMapping("/test/v1/items")
@RequiredArgsConstructor
public class ItemTestController {

	private final ItemFacadeService itemFacadeService;
	private final ItemQueryService itemQueryService;

	@Operation(
		summary = "[테스트] 아이템 추가",
		description = "쿼리 파라미터로 사용자 ID를 받아 아이템을 추가합니다. 인증 없이 userId를 직접 지정할 수 있습니다."
	)
	@PostMapping
	public ResponseEntity<BaseResponse<ItemInfoResDto>> addItem(
		@Parameter(description = "사용자 ID", example = "1")
		@RequestParam(required = false, defaultValue = "1") final Long userId,
		@Validated @RequestBody final UserItemAddReqDto reqDto
	) {
		ItemInfoResDto response = itemFacadeService.addUserItem(userId, reqDto);
		return ResponseUtils.created(response);
	}

	@Operation(
		summary = "[테스트] 아이템 상태 변경",
		description = "아이템 ID로 직접 아이템의 상태를 변경합니다 (OWNED, USED, EMPTIED). 소유자 검증을 건너뜁니다."
	)
	@PutMapping("/{id}/status")
	public ResponseEntity<BaseResponse<ItemInfoResDto>> updateItemStatus(
		@Parameter(description = "아이템 ID", example = "1")
		@PathVariable final Long id,
		@Parameter(description = "변경할 상태", example = "USED")
		@RequestParam final ItemStatus itemStatus
	) {
		Item item = itemQueryService.findById(id);
		ItemInfoResDto response = itemFacadeService.updateStatus(item.getUser().getId(), id, itemStatus);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "[테스트] 즐겨찾기 토글",
		description = "아이템 ID로 직접 즐겨찾기 상태를 토글합니다. 소유자 검증을 건너뜁니다."
	)
	@PostMapping("/{id}/favorite")
	public ResponseEntity<BaseResponse<ItemInfoResDto>> toggleFavorite(
		@Parameter(description = "아이템 ID", example = "1")
		@PathVariable final Long id
	) {
		Item item = itemQueryService.findById(id);
		ItemInfoResDto response = itemFacadeService.toggleFavorite(item.getUser().getId(), id);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "[테스트] 아이템 삭제",
		description = "아이템 ID로 직접 아이템을 소프트 삭제합니다. 소유자 검증을 건너뜁니다."
	)
	@PostMapping("/{id}/delete")
	public ResponseEntity<BaseResponse<Void>> softDeleteItem(
		@Parameter(description = "아이템 ID", example = "1")
		@PathVariable final Long id
	) {
		Item item = itemQueryService.findById(id);
		itemFacadeService.deleteItem(item.getUser().getId(), id);
		return ResponseUtils.noContent();
	}

	@Operation(
		summary = "[테스트] 사용자의 아이템 목록 조회",
		description = "쿼리 파라미터로 사용자 ID를 받아 아이템 목록을 조회합니다. 상태, 카테고리, 즐겨찾기로 필터링 가능하며 페이지네이션을 지원합니다."
	)
	@GetMapping
	public ResponseEntity<PageResponse<ItemPageResDto>> findAllItemsByUserId(
		@Parameter(description = "사용자 ID", example = "1")
		@RequestParam(required = false, defaultValue = "1") final Long userId,
		@Validated @ModelAttribute final ItemSearchReqDto searchReqDto
	) {
		Page<ItemPageResDto> response = itemFacadeService.findAllItemsByUserId(userId, searchReqDto);
		return ResponseUtils.page(response);
	}

	@Operation(
		summary = "[테스트] 아이템 상세 조회",
		description = "아이템 ID로 직접 아이템의 상세 정보를 조회합니다. 소유자 검증을 건너뜁니다."
	)
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<ItemInfoDetailResDto>> findItemById(
		@Parameter(description = "아이템 ID", example = "1")
		@PathVariable final Long id
	) {
		Item item = itemQueryService.findById(id);
		ItemInfoDetailResDto response = itemFacadeService.findByIdWithDetail(item.getUser().getId(), id);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "[테스트] 사용자의 아이템 변경 이력 조회",
		description = "쿼리 파라미터로 사용자 ID를 받아 아이템 변경 이력을 조회합니다. 페이지네이션을 지원합니다."
	)
	@GetMapping("/histories")
	public ResponseEntity<PageResponse<ItemHistoryResDto>> getItemHistories(
		@Parameter(description = "사용자 ID", example = "1")
		@RequestParam(required = false, defaultValue = "1") final Long userId,
		@Validated @ModelAttribute final ItemHistoryReqDto reqDto
	) {
		Page<ItemHistoryResDto> response = itemFacadeService.getItemHistories(userId, reqDto);
		return ResponseUtils.page(response);
	}
}
