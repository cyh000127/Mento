package com.mento.common.ai.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.AiException;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class ConsultingReportAiServiceImpl implements AiService<String> {

	private final ChatClient chatClient;

	@Override
	public String execute(
		final Resource system,
		final PromptTemplate promptTemplate,
		final BeanOutputConverter<String> converter
	) {
		String response = chatClient.prompt()
			.system(system)
			.user(promptTemplate.render() + converter.getFormat())
			.call()
			.content();

		if (response == null || response.trim().isEmpty()) {
			log.error("[AI] AI 응답이 비어있습니다.");
			throw new AiException(ErrorCode.BAD_REQUEST);
		}
		return converter.convert(response);
	}
}