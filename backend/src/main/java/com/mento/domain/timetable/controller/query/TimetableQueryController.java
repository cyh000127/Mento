package com.mento.domain.timetable.controller.query;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mento.common.response.BaseResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.timetable.dto.response.MonthlyTimetableResDto;
import com.mento.domain.timetable.service.facade.TimetableFacadeService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Tag(name = "Timetable", description = "타임테이블 관리 API")
@RestController
@RequestMapping("/api/v1/timetables")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class TimetableQueryController {

	private final TimetableFacadeService timetableFacadeService;

	@Operation(
		summary = "멘토 유형별 타임테이블 목록 조회",
		description = "31일 기준으로 특정 멘토 유형의 타임테이블 슬롯을 조회합니다. 각 날짜별로 9개의 슬롯 정보(9~17시)를 반환합니다."
	)
	@GetMapping
	public ResponseEntity<BaseResponse<MonthlyTimetableResDto>> getMonthlyTimetables(
		@Parameter(description = "멘토 유형 ID (필수)", example = "1", required = true)
		@RequestParam final Long typeId
	) {
		MonthlyTimetableResDto response = timetableFacadeService.getMonthlyTimetables(typeId);
		return ResponseUtils.ok(response);
	}
}