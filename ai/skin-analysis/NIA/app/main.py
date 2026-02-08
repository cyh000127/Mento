
import os
import shutil
import uuid
import asyncio
import httpx
from fastapi import FastAPI, HTTPException, BackgroundTasks
from pydantic import BaseModel, HttpUrl
from contextlib import asynccontextmanager
from typing import Optional

# Import the analyzer
# Assuming running from root as `uv run app/main.py` or similar, need to ensure path is correct.
# If running as `uvicorn app.main:app`, app module needs to be resolvable.
try:
    from app.analysis.service_inference import ServiceAnalyzer
except ImportError:
    import sys
    sys.path.append(os.path.join(os.path.dirname(__file__), ".."))
    from app.analysis.service_inference import ServiceAnalyzer

analyzer: Optional[ServiceAnalyzer] = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    global analyzer
    print("[INFO] Initializing ServiceAnalyzer (Loading Models)...")
    analyzer = ServiceAnalyzer()
    print("[INFO] ServiceAnalyzer Ready.")
    yield
    print("[INFO] Shutting down.")

app = FastAPI(lifespan=lifespan)

class AnalyzeRequest(BaseModel):
    front_url: HttpUrl
    l30_url: HttpUrl
    r30_url: HttpUrl
    age: Optional[int] = None
    gender: Optional[str] = None

TEMP_DIR = os.path.join(os.getcwd(), "temp_uploads")
os.makedirs(TEMP_DIR, exist_ok=True)

async def download_image(client: httpx.AsyncClient, url: str, dest_path: str):
    try:
        response = await client.get(str(url), timeout=10.0)
        response.raise_for_status()
        with open(dest_path, "wb") as f:
            f.write(response.content)
        return True
    except Exception as e:
        print(f"[ERROR] Failed to download {url}: {e}")
        return False

@app.post("/analyze")
async def analyze_skin(request: AnalyzeRequest):
    if analyzer is None:
        raise HTTPException(status_code=503, detail="Service not initialized")

    request_id = str(uuid.uuid4())
    req_dir = os.path.join(TEMP_DIR, request_id)
    os.makedirs(req_dir, exist_ok=True)

    f_path = os.path.join(req_dir, "front.jpg")
    l_path = os.path.join(req_dir, "l30.jpg")
    r_path = os.path.join(req_dir, "r30.jpg")

    try:
        async with httpx.AsyncClient() as client:
            tasks = [
                download_image(client, request.front_url, f_path),
                download_image(client, request.l30_url, l_path),
                download_image(client, request.r30_url, r_path)
            ]
            results = await asyncio.gather(*tasks)

        if not all(results):
             raise HTTPException(status_code=400, detail="Failed to download one or more images.")

        loop = asyncio.get_running_loop()
        result = await loop.run_in_executor(None, analyzer.analyze, f_path, l_path, r_path)
        
        return result

    except HTTPException as he:
        raise he
    except Exception as e:
        print(f"[ERROR] Analysis failed: {e}")
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        # Cleanup
        if os.path.exists(req_dir):
            shutil.rmtree(req_dir)

@app.get("/health")
def health_check():
    return {"status": "ok", "model_loaded": analyzer is not None}

if __name__ == "__main__":
    import uvicorn
    uvicorn.run("app.main:app", host="0.0.0.0", port=4000, reload=True)
