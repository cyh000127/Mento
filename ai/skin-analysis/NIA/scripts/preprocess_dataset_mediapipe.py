import cv2
import json
import mediapipe as mp
import numpy as np
import os
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
from tqdm import tqdm

BASE_DIR = os.getcwd()
LABEL_DIR = os.path.join(BASE_DIR, "dataset/label")
IMG_DIR = os.path.join(BASE_DIR, "dataset/img")
OUT_DIR = os.path.join(BASE_DIR, "dataset/cropped_aligned")
MODEL_PATH = os.path.join(BASE_DIR, "app/resources/face_landmarker.task")

RULES = {
    5: (101, 0.1884),  # l_cheek
    6: (280, 0.1928),  # r_cheek
    1: (10, 0.2983),   # forehead
    2: (9, 0.1011),    # glabellus
    8: (200, 0.4106),  # chin
    3: (35, 0.1187),   # l_perocular
    4: (265, 0.1206),  # r_perocular
    7: (14, 0.2005),   # lip
}

base_options = python.BaseOptions(model_asset_path=MODEL_PATH)
options = vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
landmarker = vision.FaceLandmarker.create_from_options(options)

def crop_with_landmark(img, part_id):
    if part_id == 0:
        return img
    
    mp_img = mp.Image(image_format=mp.ImageFormat.SRGB, data=cv2.cvtColor(img, cv2.COLOR_BGR2RGB))
    res = landmarker.detect(mp_img)
    
    if not res.face_landmarks:
        return None
    
    lms = res.face_landmarks[0]
    h, w = img.shape[:2]
    points = np.array([(lm.x * w, lm.y * h) for lm in lms])
    face_width = np.max(points[:, 0]) - np.min(points[:, 0])
    
    lm_id, ratio = RULES[part_id]
    cx, cy = points[lm_id]
    half = int(ratio * face_width)
    
    x1, y1 = max(0, int(cx - half)), max(0, int(cy - half))
    x2, y2 = min(w, int(cx + half)), min(h, int(cy + half))
    
    return img[y1:y2, x1:x2] if x2 > x1 and y2 > y1 else None

def main():
    print(f"Output: {OUT_DIR}")
    
    for equ in os.listdir(LABEL_DIR):
        equ_path = os.path.join(LABEL_DIR, equ)
        if not os.path.isdir(equ_path):
            continue
        
        for sub in tqdm(os.listdir(equ_path), desc=f"Processing {equ}"):
            sub_path = os.path.join(equ_path, sub)
            if not os.path.isdir(sub_path):
                continue
            
            for fname in os.listdir(sub_path):
                if not fname.endswith(".json"):
                    continue
                
                parts = fname[:-5].split('_')
                if len(parts) < 4:
                    continue
                
                try:
                    part_id = int(parts[-1])
                except ValueError:
                    continue
                
                if part_id != 0:
                    continue
                
                save_path = os.path.join(OUT_DIR, equ, sub, fname[:-5] + ".jpg")
                if os.path.exists(save_path):
                    continue
                
                with open(os.path.join(sub_path, fname), "r") as f:
                    dat = json.load(f)
                
                src_img_path = os.path.join(IMG_DIR, equ, sub, dat["info"]["filename"])
                if not os.path.exists(src_img_path):
                    continue
                
                img = cv2.imread(src_img_path)
                if img is None:
                    continue
                
                cropped = crop_with_landmark(img, part_id)
                if cropped is not None:
                    resized = cv2.resize(cropped, (256, 256))
                    os.makedirs(os.path.dirname(save_path), exist_ok=True)
                    cv2.imwrite(save_path, resized)

    print("Done.")

if __name__ == "__main__":
    main()
