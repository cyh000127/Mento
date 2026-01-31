import asyncio
import os
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, WorkerType
from livekit import rtc

load_dotenv()

async def entrypoint(ctx: JobContext):
    print(f"--- 새로운 상담 감지: {ctx.room.name} ---")

    # 1. 방에 WebRTC로 연결
    await ctx.connect()
    print(f"[{ctx.room.name}] 에이전트가 방에 입장했습니다.")

    # 2. 참가자가 들어왔을 때 이벤트 처리
    @ctx.room.on("participant_connected")
    def on_participant_connected(participant: rtc.RemoteParticipant):
        print(f"새로운 참가자 입장: {participant.identity} ({participant.name})")

    # 3. 누군가 마이크를 켰을 때(트랙 구독)
    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            print(f"[{participant.identity}]의 음성 트랙 구독 중... 여기서 Whisper 연동 예정!")

    # 에이전트가 방에서 계속 대기하게 함
    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    # 실행 시 필요한 환경 변수:
    # LIVEKIT_URL, LIVEKIT_API_KEY, LIVEKIT_API_SECRET, LIVEKIT_REDIS_URL
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))