package com.mento.domain.notification.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

class SseEmitterRepositoryTest {

	private final SseEmitterRepository sseEmitterRepository = new SseEmitterRepository();

	@Test
	@DisplayName("같은 사용자의 SSE 연결을 emitterId별로 저장한다")
	void save_MultipleEmitters() {
		// given
		Long userId = 1L;
		SseEmitter firstEmitter = new SseEmitter();
		SseEmitter secondEmitter = new SseEmitter();

		// when
		sseEmitterRepository.save(userId, "first", firstEmitter);
		sseEmitterRepository.save(userId, "second", secondEmitter);

		// then
		Map<String, SseEmitter> result = sseEmitterRepository.findAllByUserId(userId);
		assertThat(result)
			.containsEntry("first", firstEmitter)
			.containsEntry("second", secondEmitter);
	}

	@Test
	@DisplayName("SSE 연결 해제 시 해당 emitterId만 제거한다")
	void deleteById_RemoveOnlyTargetEmitter() {
		// given
		Long userId = 1L;
		SseEmitter firstEmitter = new SseEmitter();
		SseEmitter secondEmitter = new SseEmitter();

		sseEmitterRepository.save(userId, "first", firstEmitter);
		sseEmitterRepository.save(userId, "second", secondEmitter);

		// when
		sseEmitterRepository.deleteById(userId, "first");

		// then
		Map<String, SseEmitter> result = sseEmitterRepository.findAllByUserId(userId);
		assertThat(result)
			.doesNotContainKey("first")
			.containsEntry("second", secondEmitter);
	}

	@Test
	@DisplayName("사용자의 마지막 SSE 연결이 해제되면 사용자 연결 목록도 제거한다")
	void deleteById_RemoveUserEmittersWhenEmpty() {
		// given
		Long userId = 1L;
		SseEmitter emitter = new SseEmitter();

		sseEmitterRepository.save(userId, "emitter", emitter);

		// when
		sseEmitterRepository.deleteById(userId, "emitter");

		// then
		assertThat(sseEmitterRepository.findAllByUserId(userId)).isEmpty();
	}
}
