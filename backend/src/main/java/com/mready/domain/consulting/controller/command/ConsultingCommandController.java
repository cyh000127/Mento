package com.mready.domain.consulting.controller.command;

import com.mready.common.auth.principal.AuthenticatedUser;
import com.mready.common.response.BaseResponse;
import com.mready.common.util.ResponseUtils;
import com.mready.domain.consulting.dto.LiveKitSessionResponse;
import com.mready.domain.consulting.service.ConsultingFacadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Consulting", description = "컨설팅 관리 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ConsultingCommandController {

    private final ConsultingFacadeService consultingFacadeService;

    @Operation(summary = "상담 세션 생성", description = "타임테이블 ID를 기반으로 LiveKit 상담 세션을 생성하고 토큰을 발급합니다.")
    @PostMapping("/timetables/{timetableId}/sessions")
    public ResponseEntity<BaseResponse<LiveKitSessionResponse>> createSession(
            @PathVariable Long timetableId,
            @AuthenticationPrincipal AuthenticatedUser user
    ) {
        LiveKitSessionResponse response = consultingFacadeService.createSession(timetableId, user);
        return ResponseUtils.ok(response);
    }
}
