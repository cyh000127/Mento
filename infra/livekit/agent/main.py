import asyncio
import os
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, WorkerType, stt as agents_stt
from livekit import rtc
from livekit.plugins import openai, silero

load_dotenv()

async def entrypoint(ctx: JobContext):
    print(f"--- [Room: {ctx.room.name}] 상담 에이전트 가동 (GMS 모드) ---", flush=True)

    await ctx.connect()

    # 1. STT 엔진 설정 (use_realtime=False로 HTTP POST 방식 강제)
    whisper_stt = openai.STT(
        base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
        model="whisper-1",
        use_realtime=False,
    )

    # 2. 로컬 VAD 설정 (말소리가 들릴 때만 API를 쏘도록 제어)
    vad = silero.VAD.load(
        min_speech_duration=0.1,
        min_silence_duration=0.5,
    )

    # 3. 어댑터 생성: Whisper와 VAD를 하나로 묶어 '문장 단위'로 변환해주는 노드
    stt_adapter = agents_stt.StreamAdapter(whisper_stt, vad.stream())

    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            # 새로운 오디오 트랙이 감지되면 받아쓰기 테스크 시작
            asyncio.create_task(transcribe_track(track, participant, stt_adapter))

    async def transcribe_track(track, participant, stt_node):
        audio_stream = rtc.AudioStream(track)
        # 어댑터로부터 스트림을 가져옵니다 (이 스트림은 내부적으로 배치 HTTP를 쏩니다)
        stt_stream = stt_node.stream()

        # [Producer] 마이크 프레임을 어댑터에 밀어넣기
        async def push_audio():
            async for frame_event in audio_stream:
                # audio_stream은 frame_event를 뱉으므로 .frame을 추출해서 전달
                stt_stream.push_frame(frame_event.frame)
            stt_stream.end_input()

        # [Consumer] 완성된 문장 결과 받기
        async def receive_text():
            async for event in stt_stream:
                # SpeechEventType.FINAL_TRANSCRIPT가 문장이 완성된 시점입니다.
                if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                    text = event.alternatives[0].text.strip()
                    if text:
                        print(f"[{participant.identity}]: {text}", flush=True)

        # 동시 실행 (Java의 CompletableFuture.allOf 느낌)
        await asyncio.gather(push_audio(), receive_text())

    # 연결 유지
    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))