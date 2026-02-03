package com.mento.domain.skin_analysis.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SkinAnalysisReqDto(
	@NotBlank(message = "정면 사진 URL은 필수입니다.")
	@JsonProperty("front_url")
	String frontUrl,

	@NotBlank
	@JsonProperty("l30_url")
	String l30Url,

	@NotBlank
	@JsonProperty("r30_url")
	String r30Url,

	@NotNull(message = "나이는 필수입니다.")
	@Min(value = 1, message = "유효한 나이를 입력해주세요.")
	Long age,

	@NotBlank(message = "성별은 필수입니다.")
	@Pattern(regexp = "^(male|female)$", message = "성별은 'male' 또는 'female'이어야 합니다.")
	String gender
) {
}
