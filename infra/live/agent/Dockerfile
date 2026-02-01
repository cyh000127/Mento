# 1. Python 공식 이미지 사용 (Java의 JDK 이미지와 대응)
FROM python:3.10-slim

# 2. 필수 시스템 패키지 설치 (음성 처리를 위해 ffmpeg가 필요할 수 있음)
RUN apt-get update && apt-get install -y \
    ffmpeg \
    && rm -rf /var/lib/apt/lists/*

# 3. 작업 디렉토리 설정
WORKDIR /app

# 4. 종속성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 5. 소스 코드 복사
COPY . .

# 6. 실행 명령어 (Java의 Entrypoint와 동일)
# 'dev' 대신 'start'를 사용하여 프로덕션 모드로 실행 가능합니다.
ENTRYPOINT ["python", "main.py"]

CMD ["dev"]