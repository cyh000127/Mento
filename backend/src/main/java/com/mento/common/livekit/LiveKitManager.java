package com.mento.common.livekit;

import java.util.List;

import org.springframework.stereotype.Component;

import com.mento.common.error.ErrorCode;
import com.mento.common.error.exception.BusinessException;
import com.mento.domain.user.entity.Role;

import io.livekit.server.AccessToken;
import io.livekit.server.CanPublishData;
import io.livekit.server.RoomAdmin;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import io.livekit.server.RoomServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveKitManager {

	private final LiveKitProperties liveKitProperties;
	private final RoomServiceClient roomServiceClient;

	public boolean isRoomFull(String roomName, int limit) {
		try {
			var response = roomServiceClient.listRooms(List.of(roomName)).execute();

			if (response.isSuccessful() && response.body() != null) {
				var rooms = response.body();
				if (!rooms.isEmpty()) {
					int participantCount = rooms.getFirst().getNumParticipants();
					log.info("[LiveKit] 방 인원 체크: {} -> {}/{}", roomName, participantCount, limit);
					return participantCount >= limit;
				}
			}
		} catch (Exception e) {
			log.warn("[LiveKit] 방 인원 조회 실패(방이 없거나 연결 오류): {}", e.getMessage());
		}
		return false;
	}

	public void validateRoomEntry(final String roomName, final int limit) {
		if (isRoomFull(roomName, limit)) {
			throw new BusinessException(ErrorCode.LIVEKIT_ROOM_FULL);
		}
	}

	public String createToken(
		String userId,
		String name,
		String roomName,
		Role role,
		long ttlSeconds
	) {
		log.info("[Reservation] LiveKit 토큰 생성, userId={}, roomName={}, role={}, ttlSeconds={}", userId,
			roomName, role, ttlSeconds);

		AccessToken token = new AccessToken(liveKitProperties.getApiKey(), liveKitProperties.getSecret());

		token.setName(name);
		token.setIdentity(userId);
		token.setTtl(ttlSeconds * 1000);
		token.setMetadata(role.getDescription());

		RoomJoin roomJoin = new RoomJoin(true);
		token.addGrants(roomJoin);

		RoomName roomNameGrant = new RoomName(roomName);
		token.addGrants(roomNameGrant);

		CanPublishData canPublish = new CanPublishData(true);
		token.addGrants(canPublish);

		grantRoomAdmin(role, token);

		return token.toJwt();
	}

	private void grantRoomAdmin(final Role role, final AccessToken token) {
		if (role == Role.MENTOR) {
			RoomAdmin roomAdmin = new RoomAdmin(true);
			token.addGrants(roomAdmin);
		}
	}

	public String getUrl() {
		return liveKitProperties.getHost();
	}
}
