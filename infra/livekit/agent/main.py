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
        stt_stream = stt.stream() # 1. 일단 엔진이랑 통로를 엽니다.

        # [Producer] 마이크 소리를 엔진에 계속 집어넣는 작업
        async def push_audio():
            async for frame in audio_stream:
                stt_stream.push_frame(frame)
            stt_stream.end_input()

        # [Consumer] 엔진이 번역해서 주는 텍스트를 받아오는 작업
        async def receive_text():
        async for event in stt_stream:
            if event.type == "transcript" and event.transcript.text.strip():
                timestamp = f"{event.transcript.start_time:.2f}"
                content = event.transcript.text.strip()
                # 드디어 우리가 원하는 로그 출력!
                print(f"[{timestamp}] {participant.identity}: {content}", flush=True)

        # 두 작업을 동시에 돌립니다 (Java의 Thread 두 개 돌리는 것과 비슷)
        await asyncio.gather(push_audio(), receive_text())

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))