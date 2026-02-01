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
        async def receive_text():
            async for event in stt_stream:
                if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                    # 최종 인식 결과 텍스트 추출
                    text = event.alternatives[0].text.strip()
                    if text:
                        # 타임스탬프 생성 및 출력
                        timestamp = datetime.datetime.now().strftime("%H:%M:%S")
                        print(f"[{timestamp}] [{participant.identity}]: {text}", flush=True)

            '''
            # 루프 밖에서 클라이언트를 생성하여 커넥션 풀 활용 (Java의 HttpClient Bean과 유사)
            async with httpx.AsyncClient() as client:
                async for event in stt_stream:
                    if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                        text = event.alternatives[0].text.strip()
                        
                        if text:
                            timestamp = datetime.datetime.now().strftime("%H:%M:%S")
                            print(f"[{timestamp}] [{participant.identity}]: {text}", flush=True)

                            # Java 백엔드로 전송할 데이터 패키징 (DTO)
                            payload = {
                                "roomId": ctx.room.name,        # Room 명칭
                                "userId": participant.identity,  # 발화자 ID
                                "content": text,                # 변환된 텍스트
                                "timestamp": timestamp          # 발생 시각
                            }

                            try:
                                # 환경 변수에서 URL을 읽어와 비동기 전송
                                await client.post(
                                    os.getenv("JAVA_BACKEND_URL"), 
                                    json=payload, 
                                    timeout=2.0
                                )
                            except Exception as e:
                                # 전송 실패 시에도 에이전트가 죽지 않도록 예외 처리 후 로그만 출력
                                print(f"Java 전송 에러: {e}", flush=True)
                
            '''
        try:
            await asyncio.gather(push_audio(), receive_text())
        except Exception as e:
            print(f"--- [STT 에러] {e} ---", flush=True)

    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))