package com.mready.domain.member.controller.command;

import com.mready.common.auth.principal.AuthenticatedUser;
import com.mready.common.response.BaseResponse;
import com.mready.common.util.ResponseUtils;
import com.mready.domain.member.service.command.MemberCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Account", description = "계정 관리 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AccountCommandController {

    private final MemberCommandService memberCommandService;

    @Operation(summary = "회원 탈퇴", description = "회원 탈퇴를 수행합니다 (Soft Delete).")
    @DeleteMapping("/account")
    public ResponseEntity<BaseResponse<Void>> withdraw(
            @AuthenticationPrincipal AuthenticatedUser user) {
        memberCommandService.withdraw(user.getId());
        return ResponseUtils.ok(null);
    }
}
