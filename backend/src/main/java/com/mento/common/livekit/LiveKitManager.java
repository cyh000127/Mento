package com.mento.common.livekit;

import org.springframework.stereotype.Component;

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
		String role,
		boolean isAdmin,
		long ttlSeconds
	) {
		log.info("[Consulting] LiveKit 토큰 생성, userId={}, roomName={}, role={}, isAdmin={}, ttlSeconds={}", userId,
			roomName, role, isAdmin, ttlSeconds);

		AccessToken token = new AccessToken(liveKitProperties.getApiKey(), liveKitProperties.getSecret());
		token.setName(name);
		token.setIdentity(userId);

		token.setTtl(ttlSeconds * 1000);

		RoomJoin roomJoin = new RoomJoin(true);
		token.addGrants(roomJoin);

		RoomName roomNameGrant = new RoomName(roomName);
		token.addGrants(roomNameGrant);

		if (isAdmin) {
			RoomAdmin roomAdmin = new RoomAdmin(true);
			token.addGrants(roomAdmin);
		}

		token.setMetadata(role);

		return token.toJwt();
	}

	public String getUrl() {
		return liveKitProperties.getUrl();
	}
}
