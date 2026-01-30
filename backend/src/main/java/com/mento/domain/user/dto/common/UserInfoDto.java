package com.mento.domain.user.dto.common;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserInfoDto(
	@Schema(description = "유저 ID", example = "1")
	Long id,

	@Schema(description = "유저 이름", example = "홍길동")
	String name
) {
}