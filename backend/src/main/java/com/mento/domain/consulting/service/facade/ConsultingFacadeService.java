package com.mento.domain.consulting.service.facade;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.mento.common.error.ErrorCode;
import com.mento.domain.consulting.converter.ConsultingReportConverter;
import com.mento.domain.consulting.dto.common.SummaryInfoDto;
import com.mento.domain.consulting.dto.request.ConsultingChatLogSaveReqDto;
import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.consulting.event.ConsultingReportEvent;
import com.mento.domain.consulting.exception.ConsultingException;
import com.mento.domain.consulting.factory.ChatLogFactory;
import com.mento.domain.consulting.service.query.ConsultingQueryService;
import com.mento.domain.consulting.service.query.ConsultingReportQueryService;
import com.mento.domain.consulting.vo.ChatLogEntryVo;
import com.mento.domain.reservation.constants.LiveKitConstants;
import com.mento.domain.reservation.entity.Reservation;
import com.mento.domain.reservation.service.query.ReservationQueryService;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingFacadeService {

	private static final String CHAT_LOG_KEY_PREFIX = "chat:log:";
	private static final String REPORT_LOCK_KEY_PREFIX = "report:lock:";
	private static final long REPORT_LOCK_TTL_MINUTES = 5;
	private final ConsultingQueryService consultingQueryService;
	private final ConsultingReportQueryService reportQueryService;
	private final ChatLogFactory chatLogFactory;
	private final ReservationQueryService reservationQueryService;
	private final RedisTemplate<String, ChatLogEntryVo> chatLogEntryRedisTemplate;
	private final StringRedisTemplate stringRedisTemplate;
	private final ApplicationEventPublisher eventPublisher;

	public void saveChatLogToRedis(final ConsultingChatLogSaveReqDto reqDto) {
		final ChatLogEntryVo entry = chatLogFactory.createChatLogEntry(reqDto);
		pushChatLogEntryToRedis(reqDto.roomId(), entry);
		log.info("[Consulting] 채팅 로그 Redis 저장 완료 {roomId: {}, role: {}}", reqDto.roomId(), reqDto.role());
	}

	@Transactional
	public void endConsultingSession(final String roomId) {
		List<ChatLogEntryVo> chatLogs = fetchAndConvertChatLogsFromRedis(roomId);
		if (CollectionUtils.isEmpty(chatLogs)) {
			throw new ConsultingException(ErrorCode.CHATLOG_EMPTY);
		}
		Consulting consulting = consultingQueryService.findByRoomId(roomId);
		consulting.updateChatLogs(chatLogs);
		deleteChatLogsFromRedis(roomId);
		log.info("[Consulting] 상담 세션 종료 완료 {roomId: {}, logCount: {}}", roomId, chatLogs.size());
	}

	public void generateConsultingReport(final Long reservationId) {
		if (!acquireReportLock(reservationId)) {
			throw new ConsultingException(ErrorCode.REPORT_GENERATION_IN_PROGRESS);
		}

		try {
			Reservation reservation = reservationQueryService.findWithDetailsById(reservationId);
			String roomId = LiveKitConstants.ROOM_NAME_PREFIX + reservationId;
			String mentorTypeName = reservation.getMentor().getMentorType().getTypeName();

			eventPublisher.publishEvent(new ConsultingReportEvent(this, reservation, roomId, mentorTypeName));
			log.info("[Consulting] AI 보고서 생성 요청 완료 {reservationId: {}}", reservationId);
		} catch (Exception e) {
			stringRedisTemplate.delete(REPORT_LOCK_KEY_PREFIX + reservationId);
			throw e;
		}
	}

	private boolean acquireReportLock(final Long reservationId) {
		String key = REPORT_LOCK_KEY_PREFIX + reservationId;
		return Boolean.TRUE.equals(
			stringRedisTemplate.opsForValue().setIfAbsent(key, "LOCKED", REPORT_LOCK_TTL_MINUTES, TimeUnit.MINUTES)
		);
	}

	private void pushChatLogEntryToRedis(final String roomId, final ChatLogEntryVo entry) {
		String key = buildRedisKey(roomId);
		chatLogEntryRedisTemplate.opsForList().rightPush(key, entry);

		Long size = chatLogEntryRedisTemplate.opsForList().size(key);
		if (size != null && size == 1) {
			chatLogEntryRedisTemplate.expire(key, 24, TimeUnit.HOURS);
		}
	}

	private List<ChatLogEntryVo> fetchAndConvertChatLogsFromRedis(final String roomId) {
		String key = buildRedisKey(roomId);
		List<ChatLogEntryVo> entries = chatLogEntryRedisTemplate.opsForList().range(key, 0, -1);
		log.debug("[Consulting] Redis 채팅 로그 조회 완료 {size: {}}", entries.size());
		if (CollectionUtils.isEmpty(entries)) {
			return List.of();
		}

		return entries;
	}

	private void deleteChatLogsFromRedis(final String roomId) {
		String key = buildRedisKey(roomId);
		chatLogEntryRedisTemplate.delete(key);
		log.debug("[Consulting] Redis 채팅 로그 삭제 완료 {roomId: {}}", roomId);
	}

	private String buildRedisKey(final String roomId) {
		return CHAT_LOG_KEY_PREFIX + roomId;
	}

	public SummaryInfoDto findConsultingReportById(final Long userId, final Long reportId) {
		ConsultingReport consultingReport = reportQueryService.findById(reportId);

		// validateReportOwnership(consultingReport, userId);

		return ConsultingReportConverter.toSummaryInfoDto(consultingReport);
	}

	private void validateReportOwnership(final ConsultingReport report, final Long userId) {
		Long reportOwnerId = report.getReservation().getUser().getId();
		if (!reportOwnerId.equals(userId)) {
			throw new ConsultingException(ErrorCode.REPORT_ACCESS_DENIED);
		}
	}
}


