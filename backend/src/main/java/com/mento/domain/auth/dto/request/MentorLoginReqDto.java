package com.mento.domain.auth.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "멘토 로그인 요청 정보")
public record MentorLoginReqDto(
	@Schema(description = "로그인 email", example = "mentor@example.com")
	@NotBlank(message = "로그인 Email은 필수입니다.")
	@Email(message = "올바른 이메일 형식이어야 합니다.")
	String email,

	@Schema(description = "비밀번호", example = "test1234")
	@NotBlank(message = "비밀번호는 필수입니다.")
	String password
) {
}
