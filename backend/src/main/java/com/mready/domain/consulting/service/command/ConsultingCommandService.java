package com.mready.domain.consulting.service.command;

import com.mready.common.auth.principal.AuthenticatedUser;
import com.mready.common.error.ErrorCode;
import com.mready.common.error.exception.ConsultingException;
import com.mready.common.livekit.LiveKitManager;
import com.mready.domain.consulting.converter.ConsultingConverter;
import com.mready.domain.consulting.dto.LiveKitSessionResponse;
import com.mready.domain.reservation.entity.Reservation;
import com.mready.domain.reservation.repository.ReservationRepository;
import com.mready.domain.timetable.entity.Timetable;
import com.mready.domain.timetable.repository.TimetableRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class ConsultingCommandService {

    private final LiveKitManager liveKitManager;
    private final TimetableRepository timetableRepository;
    private final ReservationRepository reservationRepository;

    public LiveKitSessionResponse createSession(Long timetableId, AuthenticatedUser user) {
        Timetable timetable = timetableRepository.findById(timetableId)
                .orElseThrow(() -> new ConsultingException(ErrorCode.TIMETABLE_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = LocalDateTime.of(timetable.getScheduledDate(), timetable.getScheduledTime());
        LocalDateTime entryStartTime = startTime.minusMinutes(10); // 10분
        LocalDateTime endTime = startTime.plusMinutes(70); // 70분

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

        String role = isMento ? "MENTO" : "USER";

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

        return ConsultingConverter.toLiveKitSessionResponse(
                timetableId,
                token,
                roomName,
                liveKitManager.getUrl(),
                role
        );
    }
}
