package com.mento.domain.mentor.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "멘토링 타입 정보 DTO")
public record MentoTypeInfoDto(
	@Schema(description = "멘토링 타입 ID", example = "1")
	Long id,

	@Schema(description = "멘토링 타입 이름", example = "취업 상담")
	String name
) {
}
