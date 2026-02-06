
import subprocess
import time
import requests
import os
import sys
import signal

def run_test():
    print("[TEST] Starting Local Image Server on Port 8001...")
    # Serve dataset/img directory
    img_server = subprocess.Popen(
        [sys.executable, "-m", "http.server", "8001", "--directory", "dataset/img"],
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL
    )
    
    print("[TEST] Starting FastAPI Server on Port 8000...")
    # Use sys.executable to run uvicorn module directly in the same venv
    api_server = subprocess.Popen(
        [sys.executable, "-m", "uvicorn", "app.main:app", "--port", "8000", "--host", "0.0.0.0"],
        stdout=subprocess.PIPE,
        stderr=subprocess.PIPE,
        text=True
    )

    try:
        # Wait for server startup
        print("[TEST] Waiting for API to be ready...")
        ready = False
        for i in range(20):
            try:
                resp = requests.get("http://localhost:8000/health")
                if resp.status_code == 200:
                    print("[TEST] API is Ready!")
                    ready = True
                    break
            except requests.exceptions.ConnectionError:
                pass
            time.sleep(2)
            
        if not ready:
            print("[TEST] API failed to start.")
            # Print stderr
            outs, errs = api_server.communicate(timeout=1)
            print(f"API STDERR: {errs}")
            return

        # Prepare Request
        # Subject 0898 from previous test
        # Local Path: dataset/img/01/0898/0898_01_F.jpg
        # Server URL: http://localhost:8001/01/0898/0898_01_F.jpg
        
        payload = {
            "front_url": "http://localhost:8001/01/0898/0898_01_F.jpg",
            "l30_url":   "http://localhost:8001/01/0898/0898_01_L30.jpg",
            "r30_url":   "http://localhost:8001/01/0898/0898_01_R30.jpg",
            "age": 25,
            "gender": "female"
        }
        
        print(f"[TEST] Sending POST /analyze with payload: {payload}")
        resp = requests.post("http://localhost:8000/analyze", json=payload, timeout=30)
        
        print(f"[TEST] Response Status: {resp.status_code}")
        if resp.status_code == 200:
            print("[TEST] Response Body:")
            print(resp.json())
        else:
            print("[TEST] Error Body:")
            print(resp.text)

    except Exception as e:
        print(f"[TEST] Exception: {e}")
    finally:
        print("[TEST] Cleaning up servers...")
        img_server.terminate()
        api_server.terminate()
        img_server.wait()
        api_server.wait()
        print("[TEST] Done.")

if __name__ == "__main__":
    run_test()
