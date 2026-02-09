package com.mento.domain.timetable.controller.command;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.timetable.service.schedule.TimetableSchedulingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Timetable", description = "타임테이블 관리 API")
@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableCommandController {

	private final TimetableSchedulingService schedulingService;

	@Operation(summary = "[개발용] 타임테이블 생성 스케줄링 수동 실행", description = "테스트 목적으로 생성 스케줄링을 수동 실행합니다")
	// @PreAuthorize("hasAnyAuthority('ADMIN')")
	@PostMapping("/schedule/generate")
	public ResponseEntity<BaseResponse<Void>> triggerGenerateTimeTableScheduling() {
		schedulingService.createScheduledTimetables();
		return ResponseUtils.noContent();
	}

	@Operation(summary = "[개발용] 타임테이블 삭제 스케줄링 수동 실행", description = "테스트 목적으로 삭제 스케줄링을 수동 실행합니다")
	// @PreAuthorize("hasAnyAuthority('ADMIN')")
	@PostMapping("/schedule/delete")
	public ResponseEntity<BaseResponse<Void>> triggerDeleteTimeTableScheduling() {
		schedulingService.deleteExpiredTimetables();
		return ResponseUtils.noContent();
	}
}
