import asyncio
import os
from dotenv import load_dotenv
from livekit.agents import JobContext, WorkerOptions, cli, WorkerType, stt as agents_stt
from livekit import rtc
from livekit.plugins import openai, silero

# 1. 환경 변수 로드 (.env 파일이 없어도 시스템 환경 변수를 우선 참조함)
load_dotenv()

async def entrypoint(ctx: JobContext):
    # 2. 에이전트 구동 로그 (flush=True로 실시간 로그 확인 보장)
    print(f"--- [Room: {ctx.room.name}] 상담 에이전트 가동 (GMS 모드) ---", flush=True)

    # 3. LiveKit 서버 연결
    await ctx.connect()

    # 4. Whisper STT 설정 (GMS 경로 최적화)
    whisper_stt = openai.STT(
        base_url="https://gms.ssafy.io/gmsapi/api.openai.com/v1",
        model="whisper-1"
    )

    # 5. Silero VAD 모델 로드 (에이전트 세션당 1회 로드하여 효율성 확보)
    # 로드 실패를 대비해 try-except는 내부 프레임워크에 맡김
    vad_model = silero.VAD.load()

    # 6. STT 어댑터 생성 (위치 기반 인자로 TypeError 방지)
    # 인자 순서: (stt_engine, vad_model)
    stt_adapter = agents_stt.StreamAdapter(whisper_stt, vad_model)

    @ctx.room.on("track_subscribed")
    def on_track_subscribed(track: rtc.Track, publication, participant):
        # 7. 오디오 트랙인 경우에만 받아쓰기 테스크 실행
        if track.kind == rtc.TrackKind.KIND_AUDIO:
            asyncio.create_task(transcribe_track(track, participant, stt_adapter))

    async def transcribe_track(track, participant, stt_node):
        # 8. 오디오 스트림 및 어댑터 스트림 생성
        audio_stream = rtc.AudioStream(track)
        stt_stream = stt_node.stream()

        # 9. [Producer] 프레임을 엔진에 푸시
        async def push_audio():
            async for event in audio_stream:
                # SDK 버전에 따라 event가 Frame일수도, Event 객체일수도 있음
                # getattr로 안전하게 frame 데이터만 추출
                frame = getattr(event, 'frame', event)
                stt_stream.push_frame(frame)
            stt_stream.end_input()

        # 10. [Consumer] 변환된 텍스트 결과 수신
        async def receive_text():
            async for event in stt_stream:
                # 문장이 확정된 시점(FINAL_TRANSCRIPT)의 텍스트만 추출
                if event.type == agents_stt.SpeechEventType.FINAL_TRANSCRIPT:
                    text = event.alternatives[0].text.strip()
                    if text:
                        # [ee90e969...]: 안녕하세요
                        print(f"[{participant.identity}]: {text}", flush=True)

        # 11. Producer와 Consumer를 병렬 실행하여 실시간성 확보
        try:
            await asyncio.gather(push_audio(), receive_text())
        except Exception as e:
            # 개별 트랙 오류가 전체 에이전트를 죽이지 않도록 예외 처리
            print(f"--- [STT Error] 트랙 처리 중 오류: {e} ---", flush=True)

    # 12. 에이전트 연결 유지 (자바의 while(true)와 유사)
    while ctx.room.connection_state == rtc.ConnectionState.CONN_CONNECTED:
        await asyncio.sleep(1)

if __name__ == "__main__":
    # 13. 워커 실행
    cli.run_app(WorkerOptions(entrypoint_fnc=entrypoint, worker_type=WorkerType.ROOM))