package com.mento.domain.consulting.event;

import java.util.stream.Collectors;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.ai.service.AiService;
import com.mento.common.config.properties.PromptProperties;
import com.mento.domain.consulting.entity.Consulting;
import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.consulting.factory.ConsultingReportFactory;
import com.mento.domain.consulting.service.command.ConsultingReportCommandService;
import com.mento.domain.consulting.service.query.ConsultingQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties({
	PromptProperties.class,
})
public class ConsultingReportEventListener {
	private static final String LINE_BREAK = "\n";
	private static final String COLON = ": ";

	private static final String REPORT_LOCK_KEY_PREFIX = "report:lock:";

	private final AiService<String> aiService;
	private final ConsultingQueryService consultingQueryService;
	private final ConsultingReportCommandService consultingReportCommandService;
	private final ConsultingReportFactory consultingReportFactory;
	private final StringRedisTemplate stringRedisTemplate;

	private final PromptProperties promptProperties;

	@Async("aiUploadThreadPoolExecutor")
	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleConsultingReportEvent(final ConsultingReportEvent event) {
		log.info("[Consulting] AI 보고서 생성 시작 {reservationId: {}, roomId: {}}", event.getReservation().getId(),
			event.getRoomId());
		try {

			String chatLogs = formatChatLogs(event.getRoomId());
			PromptTemplate promptTemplate = generatePrompt(event.getMentorTypeName(), chatLogs);
			BeanOutputConverter<String> converter = new BeanOutputConverter<>(String.class);
			String aiResult = aiService.execute(promptProperties.system(), promptTemplate, converter);
			ConsultingReport consultingReport = consultingReportFactory.createReport(aiResult);
			event.getReservation().assignConsultingReport(consultingReport);

			consultingReportCommandService.save(consultingReport);
			log.info("[Consulting] AI 보고서 생성 완료 {reservationId: {}}", event.getReservation().getId());

		} catch (Exception e) {
			log.error("[Consulting] AI 보고서 생성 실패 {reservationId: {}}", event.getReservation().getId(), e);
		} finally {
			stringRedisTemplate.delete(REPORT_LOCK_KEY_PREFIX + event.getReservation().getId());
		}
	}

	private PromptTemplate generatePrompt(final String mentorTypeName, final String chatLogsText) {
		PromptTemplate promptTemplate = new PromptTemplate(promptProperties.consulting());
		promptTemplate.add("category", mentorTypeName);
		promptTemplate.add("sttText", chatLogsText);
		return promptTemplate;
	}

	private String formatChatLogs(final String roomId) {
		Consulting consulting = consultingQueryService.findByRoomId(roomId);
		return consulting.getChatLogs().stream()
			.map(entry -> entry.role() + COLON + entry.content())
			.collect(Collectors.joining(LINE_BREAK));
	}
}
