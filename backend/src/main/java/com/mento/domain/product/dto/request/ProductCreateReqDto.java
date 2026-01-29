package com.mento.domain.product.dto.request;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProductCreateReqDto(
	@NotNull(message = "브랜드 ID는 필수입니다.")
	@Schema(description = "브랜드 ID", example = "1")
	Long brandId,

	@NotBlank(message = "올리브영 상품번호는 필수입니다.")
	@Schema(description = "올리브영 상품번호", example = "A000000184697")
	String oliveyoungGoodsNo,

	@Schema(description = "중분류", example = "스킨케어")
	String categoryMedium,

	@Schema(description = "소분류", example = "토너")
	String categorySmall,

	@NotBlank(message = "상품명은 필수입니다.")
	@Schema(description = "상품명", example = "닥터지 레드 블레미쉬 클리어 수딩 크림")
	String name,

	@Schema(description = "용량/중량", example = "70ml")
	String volume,

	@Schema(description = "주요사양", example = "모든 피부용")
	String description,

	@Schema(description = "전성분", example = "정제수, 글리세린...")
	String ingredients,

	@NotNull(message = "가격은 필수입니다.")
	@Schema(description = "가격", example = "30000")
	Integer price,

	@Schema(description = "이미지 URL", example = "https://image.oliveyoung.co.kr/...")
	String imageUrl,

	@Schema(description = "상품 URL", example = "https://www.oliveyoung.co.kr/...")
	String productUrl,

	@Schema(description = "피부타입", example = "[\"지성\", \"민감성\"]")
	List<String> skinTypes,

	@Schema(description = "관련질환", example = "[\"여드름\"]")
	List<String> relatedConditions,

	@Schema(description = "주요효능", example = "[\"진정\"]")
	List<String> benefits,

	@NotNull(message = "기본 예상 사용 기간은 필수입니다.")
	@Schema(description = "기본 예상 사용 기간(일)", example = "90")
	Integer defaultUsageDays
) {
}
