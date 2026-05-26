package com.mento.domain.notification.repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SseEmitterRepository {

	private final Map<Long, Map<String, SseEmitter>> emitters = new ConcurrentHashMap<>();

	public void save(final Long userId, final String emitterId, final SseEmitter emitter) {
		emitters.computeIfAbsent(userId, ignored -> new ConcurrentHashMap<>())
			.put(emitterId, emitter);
		log.info("[notification] SSE 연결 저장 user: {}, emitter: {}", userId, emitterId);
	}

	public void deleteById(final Long userId, final String emitterId) {
		emitters.computeIfPresent(userId, (id, userEmitters) -> {
			userEmitters.remove(emitterId);
			return userEmitters.isEmpty() ? null : userEmitters;
		});
		log.info("[notification] SSE 연결 해제 user: {}, emitter: {}", userId, emitterId);
	}

	public Map<String, SseEmitter> findAllByUserId(final Long userId) {
		Map<String, SseEmitter> userEmitters = emitters.get(userId);
		if (userEmitters == null) {
			return Map.of();
		}
		return Map.copyOf(userEmitters);
	}

}
