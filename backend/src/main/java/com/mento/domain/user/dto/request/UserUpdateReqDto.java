package com.mento.domain.user.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserUpdateReqDto(
	@Schema(description = "생년월일 (YYYY-MM-DD)", example = "2000-01-01")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Past(message = "생년월일은 과거 날짜여야 합니다.")
	LocalDate birthDate
) {
}
