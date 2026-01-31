import asyncio
import os
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, WorkerType
from livekit import rtc

load_dotenv()

async def entrypoint(ctx: JobContext):
    # 최소한의 입장 로그만 남깁니다.
    print(f"--- [Room: {ctx.room.name}] 상담 시작 및 에이전트 입장 ---", flush=True)

    await ctx.connect()
    stt = openai.STT(
        base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
        model="whisper-1"
    )

    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            asyncio.create_task(transcribe_track(track, participant, stt))

    async def transcribe_track(track, participant, stt):
        audio_stream = rtc.AudioStream(track)
        stt_stream = stt.stream()

        async def push_audio():
            async for frame in audio_stream:
                stt_stream.push_frame(frame)
            stt_stream.end_input()

        async def receive_text():
            async for event in stt_stream:
                if event.type == "transcript" and event.transcript.text.strip():
                    # [최종 포맷] 나중에 Java에서 파싱하기 좋게 정갈하게 찍습니다.
                    # 예: [12.45] user_id: 안녕하세요 상담 신청합니다.
                    timestamp = f"{event.transcript.start_time:.2f}"
                    speaker = participant.identity
                    content = event.transcript.text.strip()

                    print(f"[{timestamp}] {speaker}: {content}", flush=True)

        await asyncio.gather(push_audio(), receive_text())

    # 종료 시 로그
    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)
    print(f"--- [Room: {ctx.room.name}] 상담 종료 및 에이전트 퇴장 ---", flush=True)

if __name__ == "__main__":
    # 실행 시 필요한 환경 변수:
    # LIVEKIT_URL, LIVEKIT_API_KEY, LIVEKIT_API_SECRET, LIVEKIT_REDIS_URL
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))