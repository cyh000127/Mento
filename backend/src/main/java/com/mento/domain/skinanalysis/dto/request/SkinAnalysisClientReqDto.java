package com.mento.domain.skinanalysis.dto.request;

import java.time.LocalDate;
import java.time.Period;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;

@Builder
public record SkinAnalysisClientReqDto(
	@NotBlank(message = "정면 사진 URL은 필수입니다.")
	@JsonProperty("front_url")
	String frontUrl,

	@NotBlank
	@JsonProperty("l30_url")
	String l30Url,

	@NotBlank
	@JsonProperty("r30_url")
	String r30Url,

	@NotBlank(message = "생년월일은 필수입니다.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@JsonProperty("birth_date")
	LocalDate birthDate,

	@NotBlank(message = "성별은 필수입니다.")
	@Pattern(regexp = "^(male|female)$", message = "성별은 'male' 또는 'female'이어야 합니다.")
	String gender
) {
	public int getCalculatedAge() {
		return Period.between(birthDate, LocalDate.now()).getYears();
	}
}
