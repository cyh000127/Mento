package com.mento.domain.product.controller.query;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.service.ProductFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;



@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductQueryController {

	private final ProductFacadeService productFacadeService;

	@Operation(summary = "상품 조회", description = "상품 ID로 상세 정보를 조회합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<ProductResDto>> getProduct(
		@PathVariable final Long id
	) {
		ProductResDto response = productFacadeService.getProduct(id);
		return ResponseUtils.ok(response);
	}
}
