package com.mento.domain.notification.repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Repository
public class SseEmitterRepository {

	private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

	public void save(final Long userId, final SseEmitter emitter) {
		emitters.put(userId, emitter);
		log.info("[notification] SSE 연결 저장 user: {}", userId);
	}

	public void deleteById(final Long userId) {
		emitters.remove(userId);
		log.info("[notification] SSE 연결 해제 user: {}", userId);
	}

	public Optional<SseEmitter> findById(final Long userId) {
		return Optional.ofNullable(emitters.get(userId));
	}
}
