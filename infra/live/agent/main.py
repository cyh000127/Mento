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
    # LiveKit 서버에 연결
    await ctx.connect()

    # local_participant를 제외한 나머지 참가자 중 'agent' 키워드가 포함된 참가자가 있는지 확인
    # 만약 있다면, 이 워커는 중복 실행된 것이므로 즉시 종료합니다.
    existing_agents = [
        p for p in ctx.room.remote_participants.values()
        if "agent" in (p.identity or "").lower()
    ]

    if len(existing_agents) > 0:
        print(f" [Room: {ctx.room.name}] 이미 에이전트가 존재. (기존 SID: {existing_agents[0].sid})")
        print(f" 현재 세션(Job ID: {ctx.job.id})을 즉시 종료하고 퇴장.")
        await ctx.disconnect() # 방에서 나감
        return # 함수 종료 (아래 STT 설정 로직 등을 실행하지 않음)

    # 1. STT 엔진 설정 (한국어 고정)
    whisper_stt = openai.STT(
        base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
        model="whisper-1",
        language="ko"
    )

    # 2. VAD 모델 로드
    # VAD: 실시간 음성 스트림에서 소음을 필터링하고 유효한 '목소리 구간'만 절삭(Chunking)하여 STT로 넘겨주는 전처리 인터셉터
    vad_model = silero.VAD.load()

    # 3. StreamAdapter 생성
    # StreamAdapter: VAD를 이용해 실시간 스트림 조각들을 '문장 단위'로 조립하여 STT 엔진이 처리 가능한 형태(Chunk)로 변환해주는 어댑터
    stt_adapter = agents_stt.StreamAdapter(
        stt=whisper_stt,
        vad=vad_model
    )


    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            # 비동기 시작
            asyncio.create_task(transcribe_track(track, participant, stt_adapter))

    async def transcribe_track(track, participant, stt_node):
        # 유저의 마이크로부터 들어오는 실시간 데이터 스트림
        audio_stream = rtc.AudioStream(track)
        # STT 스트림 생성
        stt_stream = stt_node.stream()

        # 오디오 프레임을 STT 스트림에 푸시
        async def push_audio():
            async for event in audio_stream:
                frame = getattr(event, 'frame', event)
                stt_stream.push_frame(frame)
            stt_stream.end_input()

        # STT 결과 수신 및 출력
        # STT 결과 수신 및 출력 (+ Java 백엔드 전송)
        async def receive_text():
            # 동일 Docker 네트워크 내 Java 서비스 엔드포인트
            endpoint = "http://backend:8080/api/v1/consulting/session/chat-log"

            # 루프 밖에서 클라이언트를 생성하여 커넥션 풀 활용
            async with httpx.AsyncClient() as client:
                async for event in stt_stream:
                    if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                        # 1. 텍스트 추출 및 정제
                        text = event.alternatives[0].text.strip()

                        if text:
                            # 2. 데이터 준비 (기존 로그 형식 유지)
                            now = datetime.datetime.now()
                            timestamp_log = now.strftime("%H:%M:%S")
                            if "USER" in participant.identity:
                                role = "USER"
                            elif "MENTOR" in participant.identity:
                                role = "MENTOR"
                            else:
                                role = "UNKNOWN"
                            # 터미널 로그 출력 (기존 방식 유지)
                            print(f"[{timestamp_log}] [{participant.identity}]: {text}", flush=True)

                            # 3. Java 백엔드 전송 (ChatLogRequest Record 형식)
                            payload = {
                                "roomId": str(ctx.room.name),
                                "role": role,
                                "content": str(text),
                            }

                            try:
                                print(f"📡 [Backend 전송 시도] URL: {endpoint}", flush=True)

                                response = await client.post(
                                    endpoint,
                                    json=payload,
                                    timeout=2.0
                                )

                                # 응답 상태 코드 확인 (200, 201 등이면 성공)
                                if response.status_code >= 200 and response.status_code < 300:
                                    print(f"✅ [Backend 전송 성공] Status: {response.status_code}", flush=True)
                                else:
                                    print(f"⚠️ [Backend 응답 오류] Status: {response.status_code}, 내용: {response.text}", flush=True)
                            except Exception as e:
                                # 전송 실패 시에도 에이전트가 중단되지 않도록 예외 처리
                                print(f" [Java 전송 에러]: {e}", flush=True)
        try:
            await asyncio.gather(push_audio(), receive_text())
        except Exception as e:
            print(f"--- [STT 에러] {e} ---", flush=True)

    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))