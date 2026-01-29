from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse, FileResponse
from contextlib import asynccontextmanager
from fastapi.concurrency import run_in_threadpool
import shutil
import os
import uuid
import json
from app.analysis.inference import SkinAnalyzer

# Global analyzer instance
analyzer = None

@asynccontextmanager
async def lifespan(app: FastAPI):
    # Startup: Load model
    global analyzer
    print("Loading AI Models...")
    try:
        analyzer = SkinAnalyzer()
        print("Models loaded successfully.")
    except Exception as e:
        print(f"Failed to load models: {e}")
        raise e
    yield
    # Shutdown: Clean up if needed
    print("Shutting down AI Server...")

app = FastAPI(title="Skin Analysis AI Server", lifespan=lifespan)

TEMP_DIR = "temp_uploads"
os.makedirs(TEMP_DIR, exist_ok=True)

@app.get("/")
def health_check():
    return {"status": "ok", "message": "Skin Analysis AI Server is running."}

@app.post("/predict")
async def predict_skin(file: UploadFile = File(...)):
    """
    Uploads an image -> returns analysis results + processed image path
    """
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="File must be an image.")
    
    # Save uploaded file temporarily
    file_ext = os.path.splitext(file.filename)[1]
    if not file_ext: file_ext = ".jpg"
    
    unique_filename = f"{uuid.uuid4()}{file_ext}"
    file_path = os.path.join(TEMP_DIR, unique_filename)
    
    try:
        with open(file_path, "wb") as buffer:
            shutil.copyfileobj(file.file, buffer)
            
        print(f"Received file: {file_path}")
        
        # Run Inference
        if analyzer is None:
            raise HTTPException(status_code=500, detail="Model not initialized.")
            
        # Run blocking inference in a thread pool to avoid blocking the event loop
        results = await run_in_threadpool(analyzer.predict, file_path)
        
        # Result image path (created by analyzer.predict)
        result_image_path = file_path.replace(file_ext, f"_result{file_ext}")
        
        # Check if result image exists
        if not os.path.exists(result_image_path):
             return JSONResponse(content={"error": "Inference failed to generate result image.", "raw_results": results})

        # Return JSON with download URL for the processed image
        # In a real microservice, you might upload result to S3 and return URL.
        # Here we return a local path ID that can be retrieved via GET.
        
        return {
            "analysis": results,
            "result_image_id": os.path.basename(result_image_path)
        }

    except Exception as e:
        print(f"Error during prediction: {e}")
        raise HTTPException(status_code=500, detail=str(e))
    finally:
        # Cleanup original upload if needed, but keeping result for retrieval
        pass

@app.get("/result/{image_filename}")
async def get_result_image(image_filename: str):
    """
    Retrieve the processed result image
    """
    file_path = os.path.join(TEMP_DIR, image_filename)
    if os.path.exists(file_path):
        return FileResponse(file_path)
    raise HTTPException(status_code=404, detail="Image not found")
