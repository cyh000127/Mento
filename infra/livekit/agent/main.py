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

        print(f"--- [{participant.identity}] 음성 수신 중... ---", flush=True)

        recognize_stream = await stt.recognize(buffer=audio_stream)
        async for event in recognize_stream:
            if event.type == "transcript" and event.transcript.text.strip():
                timestamp = f"{event.transcript.start_time:.2f}"
                content = event.transcript.text.strip()
                print(f"[{timestamp}] {participant.identity}: {content}", flush=True)

    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))