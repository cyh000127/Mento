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

    # 1. STT 엔진 설정
    whisper_stt = openai.STT(
        base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
        model="whisper-1"
    )

    # 2. VAD 모델 로드
    vad_model = silero.VAD.load()

    # 3. StreamAdapter 생성 (인자 이름을 명확히 지정하는 것이 최신 버전의 필수 조건입니다)
    # 로그의 'takes 1 positional argument' 에러를 해결하는 유일한 방법입니다.
    stt_adapter = agents_stt.StreamAdapter(
        stt=whisper_stt,
        vad=vad_model
    )

    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            asyncio.create_task(transcribe_track(track, participant, stt_adapter))

    async def transcribe_track(track, participant, stt_node):
        audio_stream = rtc.AudioStream(track)
        stt_stream = stt_node.stream()

        async def push_audio():
            async for event in audio_stream:
                # event가 frame을 가지고 있는지 확인하여 타입 에러 방지
                frame = getattr(event, 'frame', event)
                stt_stream.push_frame(frame)
            stt_stream.end_input()

        async def receive_text():
            async for event in stt_stream:
                if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                    text = event.alternatives[0].text.strip()
                    if text:
                        print(f"[{participant.identity}]: {text}", flush=True)

        try:
            await asyncio.gather(push_audio(), receive_text())
        except Exception as e:
            print(f"--- [STT 에러] {e} ---", flush=True)

    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))