package com.mento.domain.product.controller.command;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.product.dto.request.ProductCreateReqDto;
import com.mento.domain.product.dto.response.ProductResDto;
import com.mento.domain.product.service.ProductFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Product", description = "상품 관리 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductCommandController {

	private final ProductFacadeService productFacadeService;

	@Operation(summary = "상품 등록", description = "새로운 상품을 등록합니다.")
	@PostMapping
	public ResponseEntity<BaseResponse<ProductResDto>> createProduct(
			@Valid @RequestBody final ProductCreateReqDto reqDto
	) {
		ProductResDto response = productFacadeService.createProduct(reqDto);
		return ResponseUtils.created(response);
	}
}
