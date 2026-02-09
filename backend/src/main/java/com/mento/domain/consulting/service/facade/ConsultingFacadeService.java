package com.mento.domain.consulting.service.facade;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mento.common.error.ErrorCode;
import com.mento.domain.consulting.converter.ConsultingReportConverter;
import com.mento.domain.consulting.dto.common.SummaryInfoDto;
import com.mento.domain.consulting.dto.request.ConsultingChatLogSaveReqDto;
import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.consulting.exception.ConsultingException;
import com.mento.domain.consulting.factory.ChatLogFactory;
import com.mento.domain.consulting.service.query.ConsultingReportQueryService;
import com.mento.domain.consulting.vo.ChatLogEntryVo;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingFacadeService {

	private static final String CHAT_LOG_KEY_PREFIX = "chat:log:";
	private final ConsultingReportQueryService reportQueryService;
	private final ChatLogFactory chatLogFactory;
	private final RedisTemplate<String, ChatLogEntryVo> chatLogEntryRedisTemplate;

	public void saveChatLogToRedis(final ConsultingChatLogSaveReqDto reqDto) {
		final ChatLogEntryVo entry = chatLogFactory.createChatLogEntry(reqDto);
		pushChatLogEntryToRedis(reqDto.roomId(), entry);
		log.info("[Consulting] 채팅 로그 Redis 저장 완료 {roomId: {}, role: {}}", reqDto.roomId(), reqDto.role());
	}

	private void pushChatLogEntryToRedis(final String roomId, final ChatLogEntryVo entry) {
		String key = buildRedisKey(roomId);
		chatLogEntryRedisTemplate.opsForList().rightPush(key, entry);

		Long size = chatLogEntryRedisTemplate.opsForList().size(key);
		if (size != null && size == 1) {
			chatLogEntryRedisTemplate.expire(key, 24, TimeUnit.HOURS);
		}
	}

	private String buildRedisKey(final String roomId) {
		return CHAT_LOG_KEY_PREFIX + roomId;
	}

	public SummaryInfoDto findConsultingReportById(final Long userId, final Long reportId) {
		ConsultingReport consultingReport = reportQueryService.findById(reportId);
		return ConsultingReportConverter.toSummaryInfoDto(consultingReport);
	}
}


