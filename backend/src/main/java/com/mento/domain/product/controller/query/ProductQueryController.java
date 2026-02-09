package com.mento.domain.product.controller.query;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.response.PageResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.product.dto.response.ProductListResDto;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.service.ProductFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductQueryController {

	private final ProductFacadeService productFacadeService;

	@Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<ProductResDto>> getProduct(
		@PathVariable final Long id
	) {
		ProductResDto response = productFacadeService.getProduct(id);
		return ResponseUtils.ok(response);
	}

	@Operation(summary = "상품 조회", description = "모든 상품을 목록으로 확인합니다.")
	@GetMapping
	public ResponseEntity<BaseResponse<Page<ProductListResDto>>> getProducts(
		@RequestParam(defaultValue = "0") final int page,
		@RequestParam(defaultValue = "10") final int size
	) {
		Page<ProductListResDto> response = productFacadeService.getProducts(page, size);
		return ResponseUtils.ok(response);
	}

	@Operation(summary = "상품 검색", description = "상품을 검색합니다.")
	@GetMapping("/search")
	public ResponseEntity<PageResponse<ProductListResDto>> search(
		@RequestParam(name = "keyword") final String keyword,
		@ParameterObject @PageableDefault final Pageable pageable
	) {
		Page<ProductListResDto> response = productFacadeService.search(keyword, pageable);
		return ResponseUtils.page(response);
	}
}
