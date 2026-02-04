package com.mento.domain.skinanalysis.dto.request;

import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
@Schema(description = "피부 분석 요청 DTO")
public record SkinAnalysisClientReqDto(
	@NotBlank(message = "정면 사진 URL은 필수입니다.")
	@JsonProperty("front_url")
	@Schema(description = "정면 사진 URL", example = "https://example.com/front.jpg")
	String frontUrl,

	@NotBlank
	@JsonProperty("l30_url")
	@Schema(description = "왼쪽 30도 사진 URL", example = "https://example.com/l30.jpg")
	String l30Url,

	@NotBlank
	@JsonProperty("r30_url")
	@Schema(description = "오른쪽 30도 사진 URL", example = "https://example.com/r30.jpg")
	String r30Url,

	@NotBlank(message = "생년월일은 필수입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("birth_date")
	@Schema(description = "생년월일 (YYYY-MM-DD)", example = "1995-05-20")
	LocalDate birthDate,

	@NotBlank(message = "성별은 필수입니다.")
	@Pattern(regexp = "^(male|female)$", message = "성별은 'male' 또는 'female'이어야 합니다.")
	@Schema(description = "성별", example = "male", allowableValues = {"male", "female"})
	String gender
) {
	public int getCalculatedAge() {
		return Period.between(birthDate, LocalDate.now()).getYears();
	}
}
