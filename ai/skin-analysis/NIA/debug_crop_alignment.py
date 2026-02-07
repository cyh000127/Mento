import cv2
import json
import mediapipe as mp
import numpy as np
import os
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
from tqdm import tqdm

# Setup Paths
BASE_DIR = os.getcwd()
LABEL_DIR = os.path.join(BASE_DIR, "dataset/label/01")
IMG_DIR = os.path.join(BASE_DIR, "dataset/img/01")
RULES_PATH = os.path.join(BASE_DIR, "app/resources/calibration_rules.json")
MODEL_PATH = os.path.join(BASE_DIR, "app/resources/face_landmarker.task")

# Load Resources
with open(RULES_PATH, "r") as f:
    rules = json.load(f)

# Initialize Landmarker
base_options = python.BaseOptions(model_asset_path=MODEL_PATH)
options = vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
landmarker = vision.FaceLandmarker.create_from_options(options)

# Target Part: l_cheek (ID 5)
TARGET_PART = 5 
TARGET_ANGLE = "F" # Use Front for consistency first, but also verify others if needed.

# Collect samples
samples = []
subjects = sorted(os.listdir(LABEL_DIR))[:20] # Check first 20 subjects

results = []

for sub_id in subjects:
    sub_path = os.path.join(LABEL_DIR, sub_id)
    if not os.path.isdir(sub_path): continue
    
    # Check Front view l_cheek
    json_name = f"{sub_id}_01_{TARGET_ANGLE}_0{TARGET_PART}.json"
    json_path = os.path.join(sub_path, json_name)
    
    if not os.path.exists(json_path):
        # Try alternate naming or skip
        continue
        
    with open(json_path, "r") as f:
        label_dat = json.load(f)
        
    img_name = label_dat["info"]["filename"]
    img_path = os.path.join(IMG_DIR, sub_id, img_name)
    
    if not os.path.exists(img_path): continue
    
    # Load Image
    img = cv2.imread(img_path)
    if img is None: continue
    
    # --- Manual BBox ---
    bbox = label_dat["images"]["bbox"]
    if not bbox: continue
    x1, y1, x2, y2 = map(int, bbox)
    width = x2 - x1
    height = y2 - y1
    cx_manual = (x1 + x2) / 2
    cy_manual = (y1 + y2) / 2
    manual_size = max(width, height) # The crop size used in training logic
    
    # --- Inference Logic ---
    mp_img = mp.Image(image_format=mp.ImageFormat.SRGB, data=cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    detection_result = landmarker.detect(mp_img)

    if not detection_result.face_landmarks:
        continue

    lms = detection_result.face_landmarks[0]
    h, w, _ = img.shape
    points = np.array([(lm.x * w, lm.y * h) for lm in lms])
    face_width = np.max(points[:,0]) - np.min(points[:,0])

    rule = rules[str(TARGET_PART)]
    lm_id = rule["landmark_id"]
    ratio = rule["ratio"]
    
    cx_inf, cy_inf = points[lm_id]
    infer_size = ratio * face_width * 2 # Since ratio is half-width
    
    # Shifts relative to image size
    dx = cx_inf - cx_manual
    dy = cy_inf - cy_manual
    size_ratio = infer_size / manual_size
    
    results.append({
        "dx": dx,
        "dy": dy,
        "size_ratio": size_ratio,
        "face_width": face_width
    })

if not results:
    print("No valid samples processing.")
    exit(0)

# Calculate Stats
avg_dx = np.mean([r["dx"] for r in results])
avg_dy = np.mean([r["dy"] for r in results])
avg_size_ratio = np.mean([r["size_ratio"] for r in results])

print(f"Processed {len(results)} samples.")
print(f"Average Center Shift (Inference - Manual): X={avg_dx:.2f}, Y={avg_dy:.2f}")
print(f"Average Size Ratio (Inference / Manual): {avg_size_ratio:.3f}")
