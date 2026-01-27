package com.mento.common.livekit;

import org.springframework.stereotype.Component;

import com.mento.domain.user.entity.Role;

import io.livekit.server.AccessToken;
import io.livekit.server.RoomAdmin;
import io.livekit.server.RoomJoin;
import io.livekit.server.RoomName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LiveKitManager {

	private final LiveKitProperties liveKitProperties;

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
		return liveKitProperties.getUrl();
	}
}
