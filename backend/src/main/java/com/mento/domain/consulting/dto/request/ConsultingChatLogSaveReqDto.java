package com.mento.domain.consulting.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
@Schema(description = "상담 채팅 로그 저장 요청 DTO")
public record ConsultingChatLogSaveReqDto(
	@NotBlank(message = "방 ID는 필수입니다")
	@Schema(description = "방 ID", example = "1")
	String roomId,

	@NotBlank(message = "역할은 필수입니다")
	@Schema(description = "역할", example = "USER")
	String role,

	@NotBlank(message = "채팅 메시지 내용은 필수입니다")
	@Schema(description = "채팅 메시지 내용", example = "안녕하세요")
	String content
) {
}