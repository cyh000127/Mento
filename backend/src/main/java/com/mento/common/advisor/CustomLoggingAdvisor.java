package com.mento.common.advisor;

import java.util.Optional;

import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class CustomLoggingAdvisor implements CallAdvisor {

	private static final String AI_TAG = "[AI]";
	private static final int MAX_CONTENT_LENGTH = 500;

	@Override
	public String getName() {
		return "CustomLoggingAdvisor";
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public ChatClientResponse adviseCall(
		final @NonNull ChatClientRequest request,
		final @NonNull CallAdvisorChain chain
	) {
		long startTime = System.currentTimeMillis();

		try {
			logRequest(request);
			ChatClientResponse response = chain.nextCall(request);
			long durationMs = System.currentTimeMillis() - startTime;
			logResponse(response, durationMs);
			return response;
		} catch (Exception exception) {
			long durationMs = System.currentTimeMillis() - startTime;
			logError(exception, durationMs);
			throw exception;
		}
	}
	
	private void logRequest(final ChatClientRequest request) {
		if (!log.isDebugEnabled()) {
			return;
		}

		logSeparator("AI Request");

		String promptContent = Optional.ofNullable(request.prompt())
			.map(Prompt::getContents)
			.map(this::maskSensitiveData)
			.map(this::truncateContent)
			.orElse("(empty)");

		log.debug("{} Prompt: {}", AI_TAG, promptContent);
	}

	private void logResponse(final ChatClientResponse response, final long durationMs) {
		logSeparator("AI Response");

		log.info("{} Duration: {} ms", AI_TAG, durationMs);

		// 응답 내용 로깅 (DEBUG 레벨)
		if (log.isDebugEnabled()) {
			String content = Optional.ofNullable(response.chatResponse())
				.flatMap(chatResponse -> chatResponse.getResults().stream().findFirst())
				.map(generation -> String.valueOf(generation.getOutput()))
				.map(this::truncateContent)
				.orElse("(empty)");
			log.debug("{} Content: {}", AI_TAG, content);
		}

		// 메타데이터 로깅
		Optional.ofNullable(response.chatResponse())
			.map(ChatResponse::getMetadata)
			.ifPresent(metadata -> logMetadata(metadata, response));

		logEndSeparator();
	}

	private void logMetadata(final ChatResponseMetadata metadata, final ChatClientResponse response) {
		Optional.of(metadata.getModel())
			.ifPresent(model -> log.info("{} Model: {}", AI_TAG, model));

		Optional.of(metadata.getUsage())
			.ifPresent(usage -> log.info("{} Tokens: input={}, output={}, total={}",
				AI_TAG,
				usage.getPromptTokens(),
				usage.getCompletionTokens(),
				usage.getTotalTokens()));

		Optional.ofNullable(response.chatResponse())
			.flatMap(chatResponse -> chatResponse.getResults().stream().findFirst())
			.map(generation -> generation.getMetadata().getFinishReason())
			.ifPresent(finishReason ->
				log.info("{} Finish Reason: {}", AI_TAG, finishReason));

		Optional.of(metadata.getRateLimit())
			.ifPresent(rateLimit -> log.debug("{} Rate Limit: requests={}, tokens={}, resetTime={}",
				AI_TAG,
				rateLimit.getRequestsLimit(),
				rateLimit.getTokensLimit(),
				rateLimit.getRequestsReset()));
	}

	private void logError(final Exception exception, final long durationMs) {
		logSeparator("AI Error");
		log.error("{} Error after {} ms: {}", AI_TAG, durationMs, exception.getMessage(), exception);
		logEndSeparator();
	}

	private void logSeparator(final String section) {
		log.info("============== {} ==============", section);
	}

	private void logEndSeparator() {
		log.info("==========================================");
	}

	private String maskSensitiveData(final String content) {
		if (content == null) {
			return null;
		}
		return content
			.replaceAll("(?i)sk-[A-Za-z0-9]{10,}", "sk-***")
			.replaceAll("(?i)Bearer [A-Za-z0-9-._~+/]{10,}", "Bearer ***");
	}

	private String truncateContent(final String content) {
		if (content == null || content.length() <= MAX_CONTENT_LENGTH) {
			return content;
		}
		return content.substring(0, MAX_CONTENT_LENGTH) + "... (truncated)";
	}
}