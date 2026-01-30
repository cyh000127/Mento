package com.mento.domain.mentor.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(description = "멘토 정보 DTO")
public record MentorInfoDto(
	@Schema(description = "멘토 ID", example = "1")
	Long id,

	@Schema(description = "멘토 이름", example = "홍길동")
	String name
) {
}
