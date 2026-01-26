FROM python:3.9-slim

# OpenCV dependencies
RUN apt-get update && apt-get install -y \
    libgl1-mesa-glx \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY requirements_export.txt .
RUN pip install --no-cache-dir -r requirements.txt

COPY tool/ ./tool/
# Copy local torchvision package (as requested)
# COPY torchvision/ ./torchvision/
# Copy other necessary files if any (e.g. models folder later)
# COPY models/ ./models/

EXPOSE 8000

# CMD ["uvicorn", "tool.server:app", "--host", "0.0.0.0", "--port", "8000"]
CMD ["python", "tool/inference.py", "--help"]
