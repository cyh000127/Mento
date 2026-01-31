import asyncio
import os
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, WorkerType
from livekit import rtc
from livekit.plugins import openai


load_dotenv()

async def entrypoint(ctx: JobContext):
    print(f"--- [Room: {ctx.room.name}] SSAFY GMS 전용 에이전트 시작 ---", flush=True)

    await ctx.connect()

    # 1. 스트리밍(wss)이 아닌 일반 REST API 클라이언트로 생성
    # 이 방식은 WebSocket을 사용하지 않고 HTTP POST를 사용합니다.
    stt_client = openai.STT(
        base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
        model="whisper-1"
    )

    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            asyncio.create_task(transcribe_track(track, participant, stt_client))

    async def transcribe_track(track, participant, stt):
        audio_stream = rtc.AudioStream(track)

        # 2. 핵심 로직: 실시간 스트림 대신 '문장 단위 인식' 사용
        # 라이브러리가 지원하는 가벼운 VAD(음성 감지)를 결합합니다.
        print(f"--- [{participant.identity}] 음성 수신 중... ---", flush=True)

        async for event in stt.recognize(buffer=audio_stream):
            if event.type == "transcript" and event.transcript.text.strip():
                timestamp = f"{event.transcript.start_time:.2f}"
                content = event.transcript.text.strip()
                print(f"[{timestamp}] {participant.identity}: {content}", flush=True)

    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))