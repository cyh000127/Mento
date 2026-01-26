package com.mready.domain.auth.controller.command;

import com.mready.common.response.BaseResponse;
import com.mready.common.util.ResponseUtils;
import com.mready.domain.auth.service.command.AuthCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthCommandController {

    private final AuthCommandService authCommandService;

    @Operation(summary = "로그아웃", description = "액세스 토큰과 리프레시 토큰을 블랙리스트에 추가하고 Redis에서 삭제합니다.")
    @PostMapping("/logout")
    public ResponseEntity<BaseResponse<Void>> logout(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authCommandService.logout(request, response);
        return ResponseUtils.noContent();
    }

    @Operation(summary = "토큰 재발급", description = "RefreshToken을 사용하여 새로운 AccessToken과 RefreshToken을 발급합니다.")
    @PostMapping("/reissue")
    public ResponseEntity<BaseResponse<Void>> reissue(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        authCommandService.reissue(request, response);
        return ResponseUtils.noContent();
    }
}
