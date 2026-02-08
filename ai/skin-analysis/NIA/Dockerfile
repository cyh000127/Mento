# 1. uv를 이용한 빌드 준비
FROM ghcr.io/astral-sh/uv:latest AS uv_setup

# 2. 실행 스테이지 (Python 3.12)
FROM python:3.12-slim

# Set Environment Variables
ENV PYTHONDONTWRITEBYTECODE=1
ENV PYTHONUNBUFFERED=1

WORKDIR /app

# uv 복사
COPY --from=uv_setup /uv /bin/uv

# 시스템 라이브러리 (최소화하되 필수 라이브러리 포함)
RUN apt-get update && apt-get install -y \
    libglib2.0-0 \
    libxcb1 \
    libx11-6 \
    libgl1 \
    && rm -rf /var/lib/apt/lists/*

# 패키지 설치 (uv를 사용하여 초고속 설치)
COPY requirements.docker.txt .
RUN uv pip install --no-cache --system -r requirements.docker.txt

# 코드 복사
COPY app ./app

# 필요한 ONNX 모델 파일만 선별 복사 (model.onnx 및 model.onnx.data만 포함되도록 .dockerignore와 협업)
# Regression Models
COPY checkpoint/regression/1st_robust/save_model/moisture/model.onnx* ./checkpoint/regression/1st_robust/save_model/moisture/
COPY checkpoint/regression/1st_robust/save_model/pore/model.onnx* ./checkpoint/regression/1st_robust/save_model/pore/
COPY checkpoint/regression/1st_robust/save_model/pigmentation/model.onnx* ./checkpoint/regression/1st_robust/save_model/pigmentation/
COPY checkpoint/regression/1st_robust/save_model/wrinkle_Ra/model.onnx* ./checkpoint/regression/1st_robust/save_model/wrinkle_Ra/

# Classification Models
COPY checkpoint/class/1st_robust/save_model/wrinkle/model.onnx* ./checkpoint/class/1st_robust/save_model/wrinkle/
COPY checkpoint/class/1st_robust/save_model/sagging/model.onnx* ./checkpoint/class/1st_robust/save_model/sagging/

# Expose Port
EXPOSE 4000

# Run Command
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "4000"]