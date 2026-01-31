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

    # 1. STT 엔진 (GMS 주소 설정)
    whisper_stt = openai.STT(
        base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
        model="whisper-1"
    )

    # 2. VAD 설정 (메모리 효율을 위해 전역적으로 1회 로드)
    vad = silero.VAD.load()

    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            stt_adapter = agents_stt.StreamAdapter(
                stt=whisper_stt,
                vad_stream=vad.stream()
            )
            asyncio.create_task(transcribe_track(track, participant, stt_adapter))

    async def transcribe_track(track, participant, stt_node):
        audio_stream = rtc.AudioStream(track)
        stt_stream = stt_node.stream()

        # [Producer]
        async def push_audio():
            async for event in audio_stream:
                if hasattr(event, 'frame'):
                    stt_stream.push_frame(event.frame)
                else:
                    stt_stream.push_frame(event)
            stt_stream.end_input()

        # [Consumer]
        async def receive_text():
            async for event in stt_stream:
                if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                    text = event.alternatives[0].text.strip()
                    if text:
                        name = getattr(participant, 'identity', 'Unknown')
                        print(f"[{name}]: {text}", flush=True)

        # 예외 발생 시 태스크가 죽지 않도록 예외 처리 추가
        try:
            await asyncio.gather(push_audio(), receive_text())
        except Exception as e:
            print(f"--- [STT Error] {e} ---", flush=True)

    # 에이전트 생존 유지
    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))