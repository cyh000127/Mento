import asyncio
import os
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, WorkerType
from livekit import rtc

load_dotenv()

async def entrypoint(ctx: JobContext):
    # 1. 프로세스 시작 직후 (이게 찍히면 프로세스 실행은 성공)
    print(f"--- [DEBUG] 1. 에이전트 프로세스 진입 (Room: {ctx.room.name}) ---", flush=True)

    try:
        # 2. 연결 시도 직전
        print(f"--- [DEBUG] 2. LiveKit 연결 시도 중... ---", flush=True)

        # 여기서 멈춘다면 네트워크 주소(host.docker.internal) 문제임
        await ctx.connect()

        # 3. 연결 성공 직후
        print(f"--- [DEBUG] 3. 연결 성공! 방 이름: {ctx.room.name} ---", flush=True)
    except Exception as e:
        print(f"--- [DEBUG] 연결 중 에러 발생: {e} ---", flush=True)
        return

    # 4. 참가자 감지 이벤트 핸들러 등록
    @ctx.room.on("participant_connected")
    def on_participant_connected(participant: rtc.RemoteParticipant):
        print(f"--- [DEBUG] 4. 새로운 참가자 입장 감지: {participant.identity} ---", flush=True)

    # 5. 오디오 트랙 감지
    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            print(f"--- [DEBUG] 5. [{participant.identity}]의 음성 트랙 구독 중! ---", flush=True)

    # 6. 루프 진입
    print("--- [DEBUG] 6. 에이전트 대기 루프 시작 (정상 작동 중) ---", flush=True)

    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

    print("--- [DEBUG] 7. 연결이 종료되어 에이전트가 나갑니다. ---", flush=True)

if __name__ == "__main__":
    # 실행 시 필요한 환경 변수:
    # LIVEKIT_URL, LIVEKIT_API_KEY, LIVEKIT_API_SECRET, LIVEKIT_REDIS_URL
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))