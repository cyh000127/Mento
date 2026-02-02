# Base Image
# Using python slim for smaller size, but opencv/mediapipe might need libraries.
FROM python:3.10-slim

# Set Working Directory
WORKDIR /app

# Install System Dependencies (for OpenCV/Mediapipe)
RUN apt-get update && apt-get install -y \
    libglib2.0-0 \
    && rm -rf /var/lib/apt/lists/*

# Install Python Dependencies
COPY requirements.docker.txt .
RUN pip install --no-cache-dir -r requirements.docker.txt

# Copy Codebase
# Copy app directory (Source Code)
COPY app ./app

# Copy Checkpoints (Models)
# Only copy the necessary folders to keep image size optimized
COPY checkpoint/regression/1st_coat/save_model ./checkpoint/regression/1st_coat/save_model
COPY checkpoint/class/1st_coat/save_model ./checkpoint/class/1st_coat/save_model
# Ensure the structure matches what python code expects
# Code expects: ../checkpoint relative to app/analysis
# In container: WORKDIR /app. Code in /app/app/analysis.
# So ../checkpoint resolves to /app/checkpoint. This structure is correct.

# Create Temp directory
RUN mkdir -p temp_uploads

# Expose Port
EXPOSE 8000

# Run Command
# Using python -m to avoid path issues
CMD ["python", "-m", "uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8000"]
