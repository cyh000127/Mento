FROM python:3.9-slim

ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

WORKDIR /app

COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY . .

# 컨테이너 내부에서 1000번 포트를 사용하겠다고 명시
EXPOSE 1000

# uvicorn 실행 시 포트를 1000으로 지정합니다.
CMD ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "1000"]