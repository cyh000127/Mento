package com.mento.domain.reservation.service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.ReservationException;
import com.mento.common.file.dto.FileInfo;
import com.mento.common.file.service.FileService;
import com.mento.common.livekit.LiveKitManager;
import com.mento.common.livekit.dto.LiveKitSessionResponse;
import com.mento.common.util.PageUtils;
import com.mento.common.util.TimeUtils;
import com.mento.domain.reservation.constants.LiveKitConstants;
import com.mento.domain.reservation.converter.ReservationConverter;
import com.mento.domain.reservation.dto.request.ReservationHistoryReqDto;
import com.mento.domain.reservation.dto.response.MediaUploadResDto;
import com.mento.domain.reservation.dto.response.ReservationDetailResDto;
import com.mento.domain.reservation.dto.response.ReservationDraftResDto;
import com.mento.domain.reservation.dto.response.ReservationPageInfoDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.enums.ReservationStatus;
import com.mento.domain.reservation.factory.ReservationFactory;
import com.mento.domain.reservation.service.command.ReservationCommandService;
import com.mento.domain.reservation.service.query.ReservationQueryService;
import com.mento.domain.reservation.validator.ReservationValidator;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.entity.TimetableSlot;
import com.mento.domain.timetable.service.query.TimetableSlotQueryService;
import com.mento.domain.user.entity.Role;
import com.mento.domain.user.entity.User;
import com.mento.domain.user.service.query.UserQueryServiceImpl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationFacadeService {

	private final ReservationQueryService reservationQueryService;
	private final ReservationCommandService reservationCommandService;
	private final TimetableSlotQueryService timetableSlotQueryService;
	private final UserQueryServiceImpl userQueryService;

	private final FileService fileService;
	private final LiveKitManager liveKitManager;
	private final ReservationValidator reservationValidator;
	private final ReservationFactory reservationFactory;

	@Transactional
	public MediaUploadResDto uploadFiles(final List<MultipartFile> files, final Long reservationId) {
		validateReservationExists(reservationId);
		String directory = buildUploadDirectory(reservationId);
		List<FileInfo> uploadedFiles = fileService.uploadFiles(files, directory);
		log.info("[Reservation] 미디어 파일 업로드 완료 {id: {}, count: {}}", reservationId, uploadedFiles.size());
		return ReservationConverter.toMediaUploadResDto(reservationId, uploadedFiles);
	}

	private String buildUploadDirectory(final Long reservationId) {
		return LiveKitConstants.RESERVATION_DIRECTORY + reservationId;
	}

	@Transactional(readOnly = true)
	public LiveKitSessionResponse createSession(final Long reservationId, final AuthenticatedUser authuser) {
		Reservation reservation = reservationQueryService.findById(reservationId);
		Timetable timetable = reservation.getSlot().getTimetable();

		LocalDateTime now = TimeUtils.nowAsLocalDateTime();
		LocalDateTime startTime = calculateStartTime(timetable);
		// LocalDateTime entryStartTime = startTime.minusMinutes(LiveKitConstants.EARLY_ENTRY_MINUTES);
		LocalDateTime endTime = startTime.plusMinutes(LiveKitConstants.END_MINUTES);

		// validateSessionTiming(now, entryStartTime, endTime);
		Role role = Role.fromString(authuser.getRole());

		long ttlSeconds = calculateTokenTtl(now, endTime);
		String roomName = generateRoomName(reservationId);

		String uniqueId = String.format("%s(%s)",
			authuser.getId(),
			authuser.getRole());

		String token = liveKitManager.createToken(uniqueId, authuser.getEmail(), roomName, role,
			ttlSeconds);

		log.info("[Reservation] LiveKit 세션 생성 완료 {reservationId: {}, userId: {}, role: {}}", reservationId,
			authuser.getId(), role);

		return LiveKitSessionResponse.of(reservationId, token, roomName, liveKitManager.getUrl(),
			role.getDescription());
	}

	private LocalDateTime calculateStartTime(final Timetable timetable) {
		return LocalDateTime.of(timetable.getScheduledDate(), timetable.getScheduledTime());
	}

	// private void validateSessionTiming(
	// 	final LocalDateTime now,
	// 	final LocalDateTime entryStartTime,
	// 	final LocalDateTime endTime
	// ) {
	// 	if (now.isBefore(entryStartTime)) {
	// 		throw new ReservationException(ErrorCode.NOT_STARTED_YET);
	// 	}
	// 	if (now.isAfter(endTime)) {
	// 		throw new ReservationException(ErrorCode.CONSULTING_ENDED);
	// 	}
	// }

	private long calculateTokenTtl(final LocalDateTime now, final LocalDateTime endTime) {
		long ttlSeconds = Duration.between(now, endTime).getSeconds();
		if (ttlSeconds <= 0) {
			throw new ReservationException(ErrorCode.CONSULTING_ENDED);
		}
		return ttlSeconds;
	}

	private String generateRoomName(final Long reservationId) {
		return LiveKitConstants.ROOM_NAME_PREFIX + reservationId;
	}

	private void validateReservationExists(final Long id) {
		if (!reservationQueryService.existById(id)) {
			throw new ReservationException(ErrorCode.RESERVATION_NOT_FOUND);
		}
	}

	public ReservationDetailResDto findById(final AuthenticatedUser authUser, final Long id) {
		Reservation reservation = reservationQueryService.findWithDetailsById(id);
		reservationValidator.validateReservationAccess(authUser, reservation);
		return ReservationConverter.toReservationDetailResDto(reservation);
	}

	public Page<ReservationPageInfoDto> findAllByUserIdAndDateRange(
		final Long userId,
		final ReservationHistoryReqDto reqDto
	) {
		Pageable pageable = PageUtils.getPageableOrDefault(reqDto.page(), reqDto.size());

		Page<Reservation> reservations = reservationQueryService.findAllByUserIdAndStatusWithPageable(
			userId,
			reqDto.status(),
			reqDto.startDate(),
			reqDto.endDate(),
			pageable
		);
		return ReservationConverter.toReservationPageResDto(reservations);
	}

	@Transactional
	public ReservationDraftResDto createDraftReservation(final Long userId, final Long slotId) {
		User user = userQueryService.findById(userId);
		TimetableSlot timetableSlot = timetableSlotQueryService.findById(slotId);

		validateSlotAvailability(timetableSlot);
		validateNoDuplicateReservation(user.getId(), timetableSlot.getId());

		Reservation reservation = reservationFactory.createReservation(user, timetableSlot);
		Reservation savedReservation = reservationCommandService.save(reservation);

		return ReservationConverter.toReservationDraftResDto(savedReservation);
	}

	@Transactional
	public ReservationDetailResDto updateReservationSurveyData(
		final AuthenticatedUser authUser,
		final Long reservationId,
		final String surveyData
	) {
		Reservation reservation = reservationQueryService.findById(reservationId);
		reservationValidator.validateReservationAccess(authUser, reservation);
		reservation.updateSurveyData(surveyData);
		return ReservationConverter.toReservationDetailResDto(reservation);
	}

	private void validateNoDuplicateReservation(final Long userId, final Long slotId) {
		boolean exists = reservationQueryService.existsByUserIdAndSlotIdAndStatusIn(
			userId,
			slotId,
			ReservationStatus.getActiveStatuses()
		);

		if (exists) {
			throw new ReservationException(ErrorCode.DUPLICATE_RESERVATION);
		}
	}

	private void validateSlotAvailability(final TimetableSlot timetableSlot) {
		Timetable timetable = timetableSlot.getTimetable();
		LocalDateTime slotDateTime = LocalDateTime.of(timetable.getScheduledDate(), timetable.getScheduledTime());
		LocalDateTime now = TimeUtils.nowAsLocalDateTime();

		if (!slotDateTime.isAfter(now)) {
			throw new ReservationException(ErrorCode.TIMETABLE_PAST_TIME);
		}

		if (!timetableSlot.isAvailable()) {
			throw new ReservationException(ErrorCode.TIMETABLE_NOT_AVAILABLE);
		}
	}
}
