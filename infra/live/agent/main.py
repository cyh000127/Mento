import os
import asyncio
import datetime
import httpx
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, WorkerType, stt as agents_stt
from livekit import rtc
from livekit.plugins import openai, silero



# 환경 변수 로드
load_dotenv()

"""
    [ JobContext ctx 내부 구조 분석 ]
    
    1. ctx.room : 현재 에이전트가 접속한 '방' 객체
       - ctx.room.name            : (String) 방의 이름 (ex: "asd3g")
       - ctx.room.sid             : (String) 방의 고유 ID (ex: "RM_ABC123")
       - ctx.room.connection_state : (Enum) 현재 연결 상태 (CONNECTED, DISCONNECTED 등)
       - ctx.room.remote_participants : (Dict) 방에 있는 다른 참여자들 목록 {identity: Participant}
       - ctx.room.local_participant  : (Participant) 에이전트 자기 자신의 정보
       - ctx.room.on(event_name, cb) : (Method) 이벤트 리스너 등록 (Observer 패턴)
    
    2. ctx.job : 현재 실행 중인 에이전트 작업(Job)의 메타데이터
       - ctx.job.id               : (String) 작업 고유 ID (ex: "AJ_8uF56C8jxDCB")
       - ctx.job.metadata         : (String) Java 백엔드에서 생성 시 전달한 커스텀 데이터 (JSON 문자열)
    
    3. 주요 메서드
       - await ctx.connect()      : 서버와 실시간 미디어 연결 수립 (Handshake)
       - await ctx.disconnect()   : 에이전트를 방에서 명시적으로 퇴장시킴
    """

async def entrypoint(ctx: JobContext):
    print(f"--- [Room: {ctx.room.name}] agent 시작 ---", flush=True)
    await ctx.connect()

    # # 종료 신호 전송을 위한 셧다운 콜백 등록 (함수 최상단에 배치)
    # async def on_shutdown():
    #     print(f"--- [Room: {ctx.room.name}] 셧다운 콜백 실행. 종료 API 호출 ---", flush=True)
    #
    #     # roomId를 경로에 포함하도록 수정
    #     room_id = str(ctx.room.name)
    #     finish_endpoint = f"http://backend:8080/api/v1/consulting/session/{room_id}/end"
    #     finish_payload = {"roomId": room_id}
    #
    #     # 셧다운 시점에는 새로운 비동기 클라이언트를 생성해서 전송
    #     async with httpx.AsyncClient() as client:
    #         try:
    #             response = await client.post(finish_endpoint, json=finish_payload, timeout=3.0)
    #             print(f"✅ [종료 신호 성공] Status: {response.status_code}", flush=True)
    #         except Exception as e:
    #             print(f"❌ [종료 신호 실패]: {e}", flush=True)
    #
    # ctx.add_shutdown_callback(on_shutdown)

    # 중복 에이전트 체크 로직
    existing_agents = [
        p for p in ctx.room.remote_participants.values()
        if "agent" in (p.identity or "").lower()
    ]
    if len(existing_agents) > 0:
        print(f" [Room: {ctx.room.name}] 이미 에이전트 존재. 퇴장.")
        await ctx.disconnect()
        return

    openai_base_url = os.getenv("OPENAI_API_BASE")

    # STT / VAD 설정
    whisper_stt = openai.STT(
        base_url=openai_base_url,
        model="whisper-1",
        language="ko"
    )
    vad_model = silero.VAD.load()
    stt_adapter = agents_stt.StreamAdapter(stt=whisper_stt, vad=vad_model)

    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            asyncio.create_task(transcribe_track(track, participant, stt_adapter))

    async def transcribe_track(track, participant, stt_node):
        audio_stream = rtc.AudioStream(track)
        stt_stream = stt_node.stream()

        async def push_audio():
            async for event in audio_stream:
                frame = getattr(event, 'frame', event)
                stt_stream.push_frame(frame)
            stt_stream.end_input()

        async def receive_text():
            endpoint = "http://backend:8080/api/v1/consulting/session/chat-log"
            async with httpx.AsyncClient() as client:
                async for event in stt_stream:
                    if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                        text = event.alternatives[0].text.strip()
                        if text:
                            now = datetime.datetime.now()
                            timestamp_log = now.strftime("%H:%M:%S")
                            role = "USER" if "USER" in participant.identity else "MENTOR" if "MENTOR" in participant.identity else "UNKNOWN"

                            print(f"[{timestamp_log}] [{participant.identity}]: {text}", flush=True)
                            payload = {"roomId": str(ctx.room.name), "role": role, "content": str(text)}

                            try:
                                response = await client.post(endpoint, json=payload, timeout=2.0)
                                if response.status_code >= 200 and response.status_code < 300:
                                    print(f"✅ [ChatLog 성공] Status: {response.status_code}", flush=True)
                            except Exception as e:
                                print(f" [Java 전송 에러]: {e}", flush=True)
        try:
            await asyncio.gather(push_audio(), receive_text())
        except Exception as e:
            print(f"--- [STT 에러] {e} ---", flush=True)

    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))