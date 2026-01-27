package com.mento.domain.reservation.service;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.ReservationException;
import com.mento.common.file.dto.FileInfo;
import com.mento.common.file.service.FileService;
import com.mento.common.livekit.LiveKitManager;
import com.mento.common.livekit.dto.LiveKitSessionResponse;
import com.mento.domain.reservation.controller.query.ReservationQueryService;
import com.mento.domain.reservation.converter.ReservationConverter;
import com.mento.domain.reservation.dto.response.MediaUploadResDto;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.service.query.TimetableQueryServiceImpl;
import com.mento.domain.user.entity.Role;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationFacadeService {

	private static final String RESERVATION_DIRECTORY = "reservations/";
	private static final int EARLY_ENTRY_MINUTES = 10;
	private static final int END_MINUTES = 10;
	public static final String ROOM_NAME_PREFIX = "room_";

	private final ReservationQueryService reservationQueryService;
	private final TimetableQueryServiceImpl timeTableQueryService;
	private final FileService fileService;

	private final LiveKitManager liveKitManager;

	@Transactional
	public MediaUploadResDto uploadFiles(final List<MultipartFile> files, final Long id) {
		validateReservationExists(id);
		String directory = RESERVATION_DIRECTORY + id;
		List<FileInfo> uploadedFiles = fileService.uploadFiles(files, directory);
		log.info("[Reservation] 미디어 파일 업로드 완료 {id: {}, count: {}}", id, uploadedFiles.size());
		return ReservationConverter.toMediaUploadResDto(id, uploadedFiles);
	}

	public LiveKitSessionResponse createSession(final Long reservationId, final AuthenticatedUser user) {
		Reservation reservation = reservationQueryService.findById(reservationId);
		Timetable timetable = timeTableQueryService.findByReservationId(reservation.getTimetableId());

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startTime = calculateStartTime(timetable);
		LocalDateTime entryStartTime = startTime.minusMinutes(EARLY_ENTRY_MINUTES);
		LocalDateTime endTime = startTime.plusMinutes(END_MINUTES);

		validateSessionTiming(now, entryStartTime, endTime);
		Role role = Role.fromString(user.getRole());

		long ttlSeconds = calculateTokenTtl(now, endTime);
		String roomName = generateRoomName(reservationId);

		String token = liveKitManager.createToken(String.valueOf(user.getId()), user.getEmail(), roomName, role,
			ttlSeconds);

		log.info("[Reservation] LiveKit 세션 생성 완료 {reservationId: {}, userId: {}, role: {}}", reservationId,
			user.getId(), role);

		return LiveKitSessionResponse.of(reservationId, token, roomName, liveKitManager.getUrl(),
			role.getDescription());
	}

	private LocalDateTime calculateStartTime(final Timetable timetable) {
		return LocalDateTime.of(timetable.getScheduledDate(), timetable.getScheduledTime());
	}

	private void validateSessionTiming(
		final LocalDateTime now,
		final LocalDateTime entryStartTime,
		final LocalDateTime endTime
	) {
		if (now.isBefore(entryStartTime)) {
			throw new ReservationException(ErrorCode.NOT_STARTED_YET);
		}
		if (now.isAfter(endTime)) {
			throw new ReservationException(ErrorCode.CONSULTING_ENDED);
		}
	}

	private long calculateTokenTtl(final LocalDateTime now, final LocalDateTime endTime) {
		long ttlSeconds = Duration.between(now, endTime).getSeconds();
		if (ttlSeconds <= 0) {
			throw new ReservationException(ErrorCode.CONSULTING_ENDED);
		}
		return ttlSeconds;
	}

	private String generateRoomName(final Long reservationId) {
		return ROOM_NAME_PREFIX + reservationId;
	}

	private void validateReservationExists(final Long id) {
		if (!reservationQueryService.existById(id)) {
			throw new ReservationException(ErrorCode.RESERVATION_NOT_FOUND);
		}
	}
}
