package com.mento.common.test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.response.BaseResponse;
import com.mento.common.response.PageResponse;
import com.mento.common.util.ResponseUtils;
import com.mento.domain.mentor.entity.MentorType;
import com.mento.domain.mentor.service.query.MentorTypeQueryService;
import com.mento.domain.reservation.dto.request.ReservationDraftReqDto;
import com.mento.domain.reservation.dto.request.ReservationHistoryReqDto;
import com.mento.domain.reservation.dto.request.ReservationSurveyUpdateReqDto;
import com.mento.domain.reservation.dto.response.MediaUploadResDto;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.dto.response.ReservationDraftResDto;
import com.mento.domain.reservation.dto.response.ReservationPageInfoDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.reservation.repository.ReservationRepository;
import com.mento.domain.reservation.service.ReservationFacadeService;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.repository.TimetableRepository;
import com.mento.domain.timetable.repository.TimetableSlotRepository;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.repository.UserRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@Tag(name = "Test - Reservation", description = "예약 테스트용 API (인증 없이 예약 ID로 직접 접근)")
@SecurityRequirements // 인증 불필요
@RestController
@RequestMapping("/test/v1/reservations")
@RequiredArgsConstructor
public class ReservationTestController {

	private final ReservationFacadeService facadeService;
	private final ReservationQueryService reservationQueryService;
	private final ReservationRepository reservationRepository;
	private final UserRepository userRepository;
	private final TimetableRepository timetableRepository;
	private final TimetableSlotRepository timetableSlotRepository;
	private final MentorTypeQueryService mentorTypeQueryService;

	@Schema(description = "즉시 예약 완료 응답 DTO")
	public record QuickReservationResponse(
		@Schema(description = "예약 ID", example = "1")
		Long reservationId,

		@Schema(description = "사용자 ID", example = "1")
		Long userId,

		@Schema(description = "사용자 이메일", example = "user@test.com")
		String userEmail,

		@Schema(description = "멘토 ID", example = "2")
		Long mentorId,

		@Schema(description = "예약 상태", example = "CONFIRMED")
		String status,

		@Schema(description = "예약 일시", example = "2026-03-15T14:00:00")
		LocalDateTime scheduledDateTime
	) {
	}

	@Operation(
		summary = "[테스트]  예약 즉시 생성 (입력 없음)",
		description = "입력 정보 없이 완료된 예약을 DB에 저장합니다. "
			+ "User, Mentor, Timetable, Slot이 자동으로 생성되며 예약 상태는 CONFIRMED입니다. "
			+ "예약 시간은 현재 시각으로부터 10분 후입니다."
	)
	@PostMapping("/quick")
	public ResponseEntity<BaseResponse<QuickReservationResponse>> createQuickReservation() {
		// 1. 사용자 자동 생성 또는 선택
		User user = userRepository.findAll().stream()
			.filter(u -> "USER".equals(u.getRole().name()))
			.findFirst()
			.orElseGet(() -> userRepository.findById(16L)
				.orElseThrow(() -> new RuntimeException("테스트용 사용자가 필요합니다. /test/v1/users/quick-dummy를 먼저 호출하세요.")));

		User mentor = userRepository.findAll().stream()
			.filter(u -> "MENTOR".equals(u.getRole().name()))
			.findFirst()
			.orElseGet(() -> {
				User newMentor = User.builder()
					.email("test-mentor@test.com")
					.name("테스트멘토")
					.password("dummy-password")
					.kakaoId("dummy-kakao-mentor-" + System.currentTimeMillis())
					.role(com.mento.domain.user.entity.Role.MENTOR)
					.birthDate(LocalDate.of(1990, 1, 1))
					.build();
				return userRepository.save(newMentor);
			});

		// 3. MentorType 가져오기
		MentorType mentorType = mentorTypeQueryService.findAll().stream()
			.findFirst()
			.orElseThrow(() -> new RuntimeException("MentorType이 필요합니다."));

		LocalDateTime scheduledDateTime = LocalDateTime.now().plusMinutes(10);
		LocalDate scheduledDate = scheduledDateTime.toLocalDate();
		LocalTime scheduledTime = scheduledDateTime.toLocalTime();

		Timetable timetable = Timetable.builder()
			.scheduledDate(scheduledDate)
			.scheduledTime(scheduledTime)
			.build();
		Timetable savedTimetable = timetableRepository.save(timetable);

		TimetableSlot slot = TimetableSlot.builder()
			.timetable(savedTimetable)
			.mentorType(mentorType)
			.maxCapacity(5)
			.currentCapacity(0)
			.build();
		TimetableSlot savedSlot = timetableSlotRepository.save(slot);

		Reservation reservation = Reservation.builder()
			.user(user)
			.mentor(mentor)
			.slot(savedSlot)
			.status(ReservationStatus.CONFIRMED)
			.confirmedAt(LocalDateTime.now())
			.surveyData("{\"test\": \"data\"}")
			.build();

		Reservation savedReservation = reservationRepository.save(reservation);

		QuickReservationResponse response = new QuickReservationResponse(
			savedReservation.getId(),
			user.getId(),
			user.getEmail(),
			mentor.getId(),
			savedReservation.getStatus().name(),
			scheduledDateTime
		);

		return ResponseUtils.created(response);
	}

	@Operation(
		summary = "[테스트] 예약 초안 생성",
		description = "특정 사용자 ID와 타임테이블 슬롯 ID로 예약 초안을 생성합니다. 15분간 유효합니다."
	)
	@PostMapping("/draft")
	public ResponseEntity<BaseResponse<ReservationDraftResDto>> createTemporaryReservation(
		@Parameter(description = "사용자 ID", example = "1")
		@RequestParam final Long userId,
		@RequestBody final ReservationDraftReqDto reqDto
	) {
		ReservationDraftResDto response = facadeService.createDraftReservation(userId, reqDto.slotId());
		return ResponseUtils.created(response);
	}

	@Operation(
		summary = "[테스트] 예약 상세 정보 조회",
		description = "예약 ID로 직접 예약의 상세 정보를 조회합니다. 소유자 검증을 건너뜁니다."
	)
	@GetMapping("/{id}")
	public ResponseEntity<BaseResponse<ReservationDetailResDto>> findReservationById(
		@Parameter(description = "예약 ID", example = "1")
		@PathVariable final Long id
	) {
		Reservation reservation = reservationQueryService.findWithDetailsById(id);
		AuthenticatedUser mockUser = AuthenticatedUser.builder()
			.id(reservation.getUser().getId())
			.email(reservation.getUser().getEmail())
			.role(reservation.getUser().getRole().name())
			.build();
		ReservationDetailResDto response = facadeService.findById(mockUser, id);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "[테스트] 사용자의 예약 목록 조회",
		description = "쿼리 파라미터로 사용자 ID를 받아 예약 목록을 조회합니다. 날짜 범위와 상태로 필터링 가능하며 페이지네이션을 지원합니다."
	)
	@GetMapping
	public ResponseEntity<PageResponse<ReservationPageInfoDto>> findAllOfTheUserReservationHistory(
		@Parameter(description = "사용자 ID", example = "1")
		@RequestParam(required = false, defaultValue = "1") final Long userId,
		@Validated @ModelAttribute final ReservationHistoryReqDto reqDto
	) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
		AuthenticatedUser mockUser = AuthenticatedUser.builder()
			.id(user.getId())
			.email(user.getEmail())
			.role(user.getRole().name())
			.build();
		Page<ReservationPageInfoDto> response = facadeService.findAllByAuthUserAndDateRange(mockUser, reqDto);
		return ResponseUtils.page(response);
	}

	@Operation(
		summary = "[테스트] 예약 정보 수정",
		description = "예약 ID로 직접 설문 데이터를 수정합니다. 소유자 검증을 건너뜁니다."
	)
	@PutMapping("/{id}/survey")
	public ResponseEntity<BaseResponse<ReservationDetailResDto>> updateReservationSurvey(
		@Parameter(description = "예약 ID", example = "1")
		@PathVariable final Long id,
		@Validated @RequestBody final ReservationSurveyUpdateReqDto reqDto
	) {
		Reservation reservation = reservationQueryService.findById(id);
		AuthenticatedUser mockUser = AuthenticatedUser.builder()
			.id(reservation.getUser().getId())
			.email(reservation.getUser().getEmail())
			.role(reservation.getUser().getRole().name())
			.build();
		ReservationDetailResDto response = facadeService.updateReservationSurveyData(
			mockUser,
			id,
			reqDto.surveyData()
		);
		return ResponseUtils.ok(response);
	}

	@Operation(
		summary = "[테스트] 예약 관련 미디어 파일 업로드",
		description = "예약 ID로 직접 이미지 및 동영상 파일을 다중 업로드합니다."
	)
	@PostMapping("/{id}/media")
	public ResponseEntity<BaseResponse<MediaUploadResDto>> uploadReservationFiles(
		@Parameter(description = "예약 ID", example = "1")
		@PathVariable final Long id,
		@Parameter(description = "업로드할 미디어 파일 목록")
		@RequestParam("files") final List<MultipartFile> files
	) {
		MediaUploadResDto response = facadeService.uploadFiles(files, id);
		return ResponseUtils.ok(response);
	}
}
