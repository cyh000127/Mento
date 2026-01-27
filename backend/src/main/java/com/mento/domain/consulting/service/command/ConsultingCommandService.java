package com.mento.domain.consulting.service.command;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.auth.principal.AuthenticatedUser;
import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.ConsultingException;
import com.mento.common.livekit.LiveKitManager;
import com.mento.domain.consulting.dto.LiveKitSessionResponse;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.repository.ReservationRepository;
import com.mento.domain.timetable.entity.Timetable;
import com.mento.domain.timetable.repository.TimetableRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ConsultingCommandService {

	private static final int EARLY_ENTRY_MINUTES = 10;
	private static final int END_MINUTES = 10;
	private final TimetableRepository timetableRepository;
	private final ReservationRepository reservationRepository;
	private final LiveKitManager liveKitManager;

	public LiveKitSessionResponse createSession(Long timetableId, AuthenticatedUser user) {
		Timetable timetable = timetableRepository.findById(timetableId)
			.orElseThrow(() -> new ConsultingException(ErrorCode.TIMETABLE_NOT_FOUND));

		LocalDateTime now = LocalDateTime.now();
		LocalDateTime startTime = LocalDateTime.of(timetable.getScheduledDate(), timetable.getScheduledTime());
		LocalDateTime entryStartTime = startTime.minusMinutes(EARLY_ENTRY_MINUTES); // 10분
		LocalDateTime endTime = startTime.plusMinutes(END_MINUTES); // 70분

		if (now.isBefore(entryStartTime)) {
			throw new ConsultingException(ErrorCode.NOT_STARTED_YET);
		}
		if (now.isAfter(endTime)) {
			throw new ConsultingException(ErrorCode.CONSULTING_ENDED);
		}

		Reservation reservation = reservationRepository.findByTimetableId(timetableId)
			.orElseThrow(() -> new ConsultingException(ErrorCode.NOT_AUTHORIZED));

		boolean isMento = user.getId().equals(reservation.getMentoId());
		boolean isUser = user.getId().equals(reservation.getUserId());

		if (!isMento && !isUser) {
			throw new ConsultingException(ErrorCode.NOT_AUTHORIZED);
		}

		String role = isMento ? "MENTOR" : "CUSTOMER";

		long ttlSeconds = Duration.between(now, endTime).getSeconds();
		if (ttlSeconds <= 0) {
			throw new ConsultingException(ErrorCode.CONSULTING_ENDED);
		}

		String roomName = "room_" + timetableId;

		String token = liveKitManager.createToken(
			String.valueOf(user.getId()),
			user.getEmail(),
			roomName,
			role,
			isMento,
			ttlSeconds
		);

		return LiveKitSessionResponse.of(
			timetableId,
			token,
			roomName,
			liveKitManager.getUrl(),
			role
		);
	}
}
