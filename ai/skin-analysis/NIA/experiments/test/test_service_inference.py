import os
import sys
import json
import cv2

# Add workspace to path
workspace_path = os.getcwd()
sys.path.insert(0, workspace_path)

from app.analysis.service_inference import ServiceAnalyzer

def test_inference():
    analyzer = ServiceAnalyzer()
    
    # Specific test images
    base_dir = "dataset/img/01/0001"
    img_f = os.path.join(base_dir, "0001_01_F.jpg")
    img_l30 = os.path.join(base_dir, "0001_01_L30.jpg")
    img_r30 = os.path.join(base_dir, "0001_01_R30.jpg")

    if not (os.path.exists(img_f) and os.path.exists(img_l30) and os.path.exists(img_r30)):
        print("One or more test images not found.")
        return

    print(f"Testing with images:\n  Front: {img_f}\n  L30: {img_l30}\n  R30: {img_r30}")
    
    # Simulate front, left, right inputs
    result = analyzer.analyze(img_f, img_l30, img_r30)
    
    print(json.dumps(result, indent=2, ensure_ascii=False))

if __name__ == "__main__":
    test_inference()
