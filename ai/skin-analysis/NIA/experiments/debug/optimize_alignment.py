import cv2
import json
import mediapipe as mp
import numpy as np
import os
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
from tqdm import tqdm
from collections import Counter

# Setup Paths
BASE_DIR = os.getcwd()
LABEL_DIR = os.path.join(BASE_DIR, "dataset/label/01")
IMG_DIR = os.path.join(BASE_DIR, "dataset/img/01")
MODEL_PATH = os.path.join(BASE_DIR, "app/resources/face_landmarker.task")

# Initialize Landmarker
base_options = python.BaseOptions(model_asset_path=MODEL_PATH)
options = vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
landmarker = vision.FaceLandmarker.create_from_options(options)

PARTS = {
    5: "l_cheek",
    6: "r_cheek",
    1: "forehead",
    2: "glabellus",
    8: "chin",
    3: "l_perocular", # eye wrinkle
    4: "r_perocular"
}

results = {pid: {"ids": [], "ratios": []} for pid in PARTS}

subjects = sorted(os.listdir(LABEL_DIR))[:50]

for sub_id in tqdm(subjects, desc="Processing"):
    sub_path = os.path.join(LABEL_DIR, sub_id)
    if not os.path.isdir(sub_path): continue
    
    # Check all JSONs in this folder
    for fname in os.listdir(sub_path):
        if not fname.endswith(".json"): continue
        
        # Parse filename: ID_01_ANGLE_PART.json
        parts = fname[:-5].split('_')
        if len(parts) < 4: continue
        
        part_id = int(parts[3])
        if part_id not in PARTS: continue
        
        angle = parts[2]
        # Skip if angle is not standard frontal/side view relevant to that part
        # Actually, let's just use what's labeled. If labeled, it's valid ground truth.
        
        json_path = os.path.join(sub_path, fname)
        with open(json_path, "r") as f:
            dat = json.load(f)
            
        img_name = dat["info"]["filename"]
        img_path = os.path.join(IMG_DIR, sub_id, img_name)
        
        if not os.path.exists(img_path): continue
        
        img = cv2.imread(img_path)
        if img is None: continue
        
        # Manual BBox
        bbox = dat["images"]["bbox"]
        if not bbox: continue
        x1, y1, x2, y2 = map(int, bbox)
        cx_m = (x1 + x2) / 2
        cy_m = (y1 + y2) / 2
        # Manual Size: Half length of the forced square
        manual_half_size = max(x2-x1, y2-y1) / 2
        
        # Landmarks
        mp_img = mp.Image(image_format=mp.ImageFormat.SRGB, data=cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
        res = landmarker.detect(mp_img)
        
        if not res.face_landmarks: continue
        lms = res.face_landmarks[0]
        h, w, _ = img.shape
        points = np.array([(lm.x * w, lm.y * h) for lm in lms])
        
        # Face Width
        face_width = np.max(points[:,0]) - np.min(points[:,0])
        
        # Best Landmark
        dists = np.linalg.norm(points - np.array([cx_m, cy_m]), axis=1)
        best_id = np.argmin(dists)
        
        # Ideal Ratio
        # ratio * face_width = manual_half_size
        ideal_ratio = manual_half_size / face_width
        
        results[part_id]["ids"].append(best_id)
        results[part_id]["ratios"].append(ideal_ratio)

print("\nOptimization Results:")
for pid, name in PARTS.items():
    ids = results[pid]["ids"]
    ratios = results[pid]["ratios"]
    
    if not ids:
        print(f"{name} ({pid}): No data.")
        continue
        
    # Consensus: Most common landmark
    counts = Counter(ids)
    best_id, freq = counts.most_common(1)[0]
    
    # Average ratio
    avg_ratio = np.mean(ratios)
    
    print(f"Part {name} (ID {pid}): Best LM={best_id} (Freq={freq}/{len(ids)}), Ratio={avg_ratio:.4f}")
