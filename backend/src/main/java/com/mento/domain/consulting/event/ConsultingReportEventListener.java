package com.mento.domain.consulting.event;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.event.EventListener;
import org.springframework.core.retry.RetryTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.ai.service.AiService;
import com.mento.common.config.properties.PromptProperties;
import com.mento.domain.consulting.entity.ConsultingReport;
import com.mento.domain.consulting.service.query.ConsultingReportQueryService;
import com.mento.domain.consulting.vo.ChatLogEntryVo;

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

	private final AiService<String> aiService;
	private final RetryTemplate aiRetryTemplate;
	private final PromptProperties promptProperties;
	private final ConsultingReportQueryService consultingReportQueryService;

	@Async("aiUploadThreadPoolExecutor")
	@EventListener
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void handleConsultingReportEvent(final ConsultingReportEvent event) {
		Long reservationId = event.getReservation().getId();
		log.info("[Consulting] AI 보고서 생성 시작 {reservationId: {}}", reservationId);

		try {
			String chatLogsText = formatChatLogs(event.getChatLogs());
			String aiResult = aiRetryTemplate.execute(() -> {
				PromptTemplate promptTemplate = generatePrompt(event.getMentorTypeName(), chatLogsText);
				BeanOutputConverter<String> converter = new BeanOutputConverter<>(String.class);
				return aiService.execute(promptProperties.system(), promptTemplate, converter);
			});
			ConsultingReport consultingReport = consultingReportQueryService.findByReservationId(reservationId);
			consultingReport.updateContent(aiResult);

			log.info("[Consulting] AI 보고서 생성 완료 {reservationId: {}}", reservationId);
		} catch (Exception e) {
			log.error("[Consulting] AI 보고서 생성 실패 {reservationId: {}}", reservationId, e);
		}
	}

	private PromptTemplate generatePrompt(final String mentorTypeName, final String chatLogsText) {
		PromptTemplate promptTemplate = new PromptTemplate(promptProperties.consulting());
		promptTemplate.add("category", mentorTypeName);
		promptTemplate.add("sttText", chatLogsText);
		return promptTemplate;
	}

	private String formatChatLogs(final List<ChatLogEntryVo> chatLogs) {
		return chatLogs.stream()
			.map(entry -> entry.role() + COLON + entry.content())
			.collect(Collectors.joining(LINE_BREAK));
	}
}
