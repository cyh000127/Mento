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
LABEL_DIR = os.path.join(BASE_DIR, "dataset/label")
IMG_DIR = os.path.join(BASE_DIR, "dataset/img")
OUT_DIR = os.path.join(BASE_DIR, "dataset/cropped_aligned")
MODEL_PATH = os.path.join(BASE_DIR, "app/resources/face_landmarker.task")

def mkdir(path):
    os.makedirs(path, exist_ok=True)

# Optimized Rules from Analysis
# Format: Part_ID: (Landmark_ID, Ratio)
RULES = {
    5: (101, 0.1884),   # l_cheek
    6: (280, 0.1928),   # r_cheek
    1: (10, 0.2983),    # forehead
    2: (9, 0.1011),     # glabellus
    8: (200, 0.4106),   # chin
    3: (35, 0.1187),    # l_perocular
    4: (265, 0.1206),   # r_perocular
    7: (14, 0.2005),    # lip (Default from old rules, assumed valid)
}

# Initialize Landmarker
base_options = python.BaseOptions(model_asset_path=MODEL_PATH)
options = vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
landmarker = vision.FaceLandmarker.create_from_options(options)

print(f"Starting Preprocessing... Output: {OUT_DIR}")

# Traverse Labels
for equ in os.listdir(LABEL_DIR):
    equ_path = os.path.join(LABEL_DIR, equ)
    if not os.path.isdir(equ_path): continue
    
    for sub in tqdm(os.listdir(equ_path), desc=f"Processing {equ}"):
        sub_path = os.path.join(equ_path, sub)
        if not os.path.isdir(sub_path): continue
        
        # We need to process each JSON to know which Part to crop from which Image
        for fname in os.listdir(sub_path):
            if not fname.endswith(".json"): continue
            
            # Parse: ID_EQU_ANGLE_PART.json
            parts = fname[:-5].split('_')
            # Example: 0001_01_F_05.json -> parts=["0001", "01", "F", "05"]
            if len(parts) < 4: continue
            
            try:
                part_id = int(parts[-1])
            except ValueError:
                continue
                
            if part_id not in RULES: continue
            
            # Load JSON
            json_path = os.path.join(sub_path, fname)
            with open(json_path, "r") as f:
                dat = json.load(f)
                
            # Load Image
            img_name = dat["info"]["filename"]
            src_img_path = os.path.join(IMG_DIR, equ, sub, img_name)
            
            if not os.path.exists(src_img_path):
                # Try finding it in sub folder directly
                # Structure is dataset/img/EQU/SUB/filename
                # Assuming standard structure
                continue
                
            img = cv2.imread(src_img_path)
            if img is None: continue
            
            # Run MediaPipe
            mp_img = mp.Image(image_format=mp.ImageFormat.SRGB, data=cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
            res = landmarker.detect(mp_img)
            
            cropped_img = None
            
            if res.face_landmarks:
                lms = res.face_landmarks[0]
                h, w, _ = img.shape
                points = np.array([(lm.x * w, lm.y * h) for lm in lms])
                face_width = np.max(points[:,0]) - np.min(points[:,0])
                
                lm_id, ratio = RULES[part_id]
                cx, cy = points[lm_id]
                
                # Crop Size
                half_size = int(ratio * face_width)
                x1 = int(cx - half_size)
                y1 = int(cy - half_size)
                x2 = int(cx + half_size)
                y2 = int(cy + half_size)
                
                # Boundary Checks
                x1 = max(0, x1)
                y1 = max(0, y1)
                x2 = min(w, x2)
                y2 = min(h, y2)
                
                if x2 > x1 and y2 > y1:
                    cropped_img = img[y1:y2, x1:x2]
            
            # Fallback: If MediaPipe fails, define behavior. 
            # Ideally, we SKIP to ensure high quality training data.
            # But if we skip too many, we lose data.
            # For now, if failed, we SKIP.
            
            if cropped_img is not None:
                # Resize to standard (224 or 256)
                # img_crop.py used 256. CustomDataset resizes to args.res (224).
                # Let's save as 256 to allow some augmentation flexibility.
                resized = cv2.resize(cropped_img, (256, 256))
                
                # Save
                # Structure: dataset/cropped_aligned/EQU/SUB/filename_PART.jpg
                # CustomDataset expects: os.path.join("dataset/cropped_img", self.i_path + ".jpg")
                # i_path is "EQU/SUB/SUB_EQU_ANGLE_AREA"
                # which corresponds to the JSON filename without extension!
                
                save_dir = os.path.join(OUT_DIR, equ, sub)
                mkdir(save_dir)
                
                # JSON: 0001_01_F_05.json
                # Save as: 0001_01_F_05.jpg
                save_name = fname[:-5] + ".jpg" # remove .json, add .jpg
                save_path = os.path.join(save_dir, save_name)
                
                cv2.imwrite(save_path, resized)

print("Done.")
