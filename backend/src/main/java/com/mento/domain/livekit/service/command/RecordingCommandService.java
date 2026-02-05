package com.mento.domain.livekit.service.command;

import java.io.IOException;

import org.jspecify.annotations.NonNull;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mento.common.config.properties.CloudflareProperties;
import com.mento.common.error.ErrorCode;
import com.mento.domain.livekit.converter.RecordingConverter;
import com.mento.domain.livekit.dto.RecordingReqDto;
import com.mento.domain.livekit.dto.RecordingResDto;
import com.mento.domain.livekit.exception.LiveKitException;

import io.livekit.server.EgressServiceClient;
import io.livekit.server.WebhookReceiver;
import livekit.LivekitEgress.EgressInfo;
import livekit.LivekitEgress.EncodedFileOutput;
import livekit.LivekitEgress.EncodedFileType;
import livekit.LivekitEgress.FileInfo;
import livekit.LivekitEgress.S3Upload;
import livekit.LivekitWebhook.WebhookEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import retrofit2.Call;
import retrofit2.Response;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordingCommandService {

	private static final String EGRESS_STARTED_EVENT = "egress_started";
	private static final String EGRESS_UPDATED_EVENT = "egress_updated";
	private static final String EGRESS_ENDED_EVENT = "egress_ended";
	private static final String EGRESS_COMPLETE_STATUS = "EGRESS_COMPLETE";
	private static final String EGRESS_FAILED_STATUS = "EGRESS_FAILED";

	private final RedisTemplate<String, String> redisTemplate;
	private final EgressServiceClient egressServiceClient;
	private final CloudflareProperties cloudflareProperties;
	private final WebhookReceiver webhookReceiver;

	public RecordingResDto startRecording(final RecordingReqDto request) {
		String roomId = request.roomId();
		log.info("[Recording] 녹화 시작 요청 {roomId: {}}", roomId);

		EncodedFileOutput fileOutput = buildFileOutput(roomId);
		EgressInfo egressInfo = executeStartEgress(request, fileOutput);

		log.info("[Recording] 녹화 시작 완료 {egressId: {}, roomId: {}}", egressInfo.getEgressId(), roomId);
		redisTemplate.opsForValue().set(roomId, egressInfo.getEgressId());
		return RecordingConverter.toStartResDto(egressInfo.getEgressId(), roomId);
	}

	public RecordingResDto stopRecording(final String roomId) {
		String egressId = redisTemplate.opsForValue().getAndDelete(roomId);
		log.info("[Recording] 녹화 중지 요청 {roomId: {}, egressId: {}}", roomId, egressId);

		executeStopEgress(egressId);
		log.info("[Recording] 녹화 중지 완료 {roomId: {}, egressId: {}}", roomId, egressId);

		return RecordingConverter.toStopResDto(egressId);
	}

	public void handleWebhook(final String body, final String authHeader) {
		log.info("[Recording] Webhook 수신");
		try {
			WebhookEvent event = webhookReceiver.receive(body, authHeader);
			processWebhookEvent(event);
		} catch (Exception e) {
			log.error("[Recording] Webhook 검증 실패 {error: {}}", e.getMessage());
			throw new LiveKitException(ErrorCode.WEBHOOK_VERIFICATION_FAILED);
		}
	}

	private void processWebhookEvent(final WebhookEvent event) {
		String eventType = event.getEvent();
		EgressInfo egressInfo = event.getEgressInfo();

		switch (eventType) {
			case EGRESS_STARTED_EVENT -> handleEgressStarted(egressInfo);
			case EGRESS_UPDATED_EVENT -> handleEgressUpdated(egressInfo);
			case EGRESS_ENDED_EVENT -> handleEgressEnded(egressInfo);
			default -> log.debug("[Recording] 처리하지 않는 이벤트 타입 {eventType: {}}", eventType);
		}
	}

	private void handleEgressStarted(final EgressInfo egressInfo) {
		log.info("[Recording] Egress 시작 {egressId: {}, roomName: {}}",
			egressInfo.getEgressId(), egressInfo.getRoomName());
	}

	private void handleEgressUpdated(final EgressInfo egressInfo) {
		log.info("[Recording] Egress 업데이트 {egressId: {}, status: {}}",
			egressInfo.getEgressId(), egressInfo.getStatus().name());
	}

	private void handleEgressEnded(final EgressInfo egressInfo) {
		String egressId = egressInfo.getEgressId();
		String status = egressInfo.getStatus().name();

		log.info("[Recording] Egress 종료 이벤트 수신 {egressId: {}, status: {}}", egressId, status);

		if (EGRESS_COMPLETE_STATUS.equals(status)) {
			handleEgressComplete(egressInfo);
		} else if (EGRESS_FAILED_STATUS.equals(status)) {
			log.error("[Recording] 녹화 실패 {egressId: {}, error: {}}", egressId, egressInfo.getError());
		}
	}

	private EncodedFileOutput buildFileOutput(final String roomId) {
		S3Upload r2Output = S3Upload.newBuilder()
			.setEndpoint(cloudflareProperties.endpoint())
			.setAccessKey(cloudflareProperties.accessKey())
			.setSecret(cloudflareProperties.secretKey())
			.setBucket(cloudflareProperties.bucket())
			.build();

		return EncodedFileOutput.newBuilder()
			.setFileType(EncodedFileType.MP4)
			.setFilepath("recordings/" + roomId + "_{time}.mp4")
			.setS3(r2Output)
			.build();
	}

	private EgressInfo executeStartEgress(final RecordingReqDto reqDto, final EncodedFileOutput fileOutput) {
		try {
			Response<EgressInfo> response = buildEgressInfoCall(reqDto, fileOutput).execute();
			validateResponse(response, ErrorCode.RECORDING_START_FAILED);
			return response.body();
		} catch (IOException e) {
			log.error("[Recording] 녹화 시작 실패 {roomId: {}, error: {}}", reqDto.roomId(), e.getMessage());
			throw new LiveKitException(ErrorCode.RECORDING_START_FAILED);
		}
	}

	private @NonNull Call<EgressInfo> buildEgressInfoCall(
		final RecordingReqDto reqDto,
		final EncodedFileOutput fileOutput
	) {
		return egressServiceClient.startTrackCompositeEgress(
			reqDto.roomId(),
			fileOutput,
			reqDto.audioTrackSid(),
			reqDto.videoTrackSid()
		);
	}

	private void executeStopEgress(final String egressId) {
		try {
			Response<EgressInfo> response = egressServiceClient.stopEgress(egressId).execute();
			validateResponse(response, ErrorCode.RECORDING_STOP_FAILED);
		} catch (IOException e) {
			log.error("[Recording] 녹화 중지 실패 {egressId: {}, error: {}}", egressId, e.getMessage());
			throw new LiveKitException(ErrorCode.RECORDING_STOP_FAILED);
		}
	}

	private void validateResponse(final Response<EgressInfo> response, final ErrorCode errorCode) {
		if (!response.isSuccessful() || response.body() == null) {
			throw new LiveKitException(errorCode);
		}
	}

	private void handleEgressComplete(final EgressInfo egressInfo) {
		if (egressInfo.getFileResultsList().isEmpty()) {
			return;
		}
		FileInfo fileInfo = egressInfo.getFileResultsList().getFirst();
		log.info("[Recording] 녹화 성공 {filePath: {}, fileSize: {} bytes}",
			fileInfo.getLocation(), fileInfo.getSize());
	}
}
