package com.mready.domain.member.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

import com.mready.domain.member.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
public record MemberResDto(
	@Schema(description = "회원 ID", example = "1")
	Long id,

	@Schema(description = "회원 이름", example = "홍길동")
	String name,

	@Schema(description = "회원 이메일", example = "hong@example.com")
	String email,

	@Schema(description = "생년월일", example = "1990-01-01")
    LocalDate birthDate,

	@Schema(description = "권한", example = "USER")
	Role role,

	@Schema(description = "생성 시각")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime createdAt,

	@Schema(description = "수정 시각")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
	LocalDateTime updatedAt
) {
}
