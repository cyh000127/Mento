import os
import json
import cv2
import numpy as np
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
from tqdm import tqdm
from collections import defaultdict

FACE_PARTS = {
    1: "forehead", 2: "glabellus", 3: "l_perocular", 4: "r_perocular",
    5: "l_cheek", 6: "r_cheek", 7: "lip", 8: "chin"
}

EQU_MAPPING = {"01": "digital_camera", "02": "smart_pad", "03": "smart_phone"}

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
RESOURCE_DIR = os.path.join(os.path.dirname(os.path.dirname(CURRENT_DIR)), "app", "resources")

def get_landmarker():
    base_options = python.BaseOptions(model_asset_path=os.path.join(RESOURCE_DIR, 'face_landmarker.task'))
    return vision.FaceLandmarker.create_from_options(
        vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
    )

def calculate_rules(dataset_root="dataset", sample_limit=100):
    label_root = os.path.join(dataset_root, "label")
    img_root = os.path.join(dataset_root, "img")
    
    samples = defaultdict(list)
    
    print("Collecting samples...")
    for equ in os.listdir(label_root):
        equ_path = os.path.join(label_root, equ)
        if not os.path.isdir(equ_path):
            continue
        
        for sub in os.listdir(equ_path):
            sub_path = os.path.join(equ_path, sub)
            if not os.path.isdir(sub_path):
                continue

            for anno_file in os.listdir(sub_path):
                if not anno_file.endswith(".json"):
                    continue
                
                try:
                    with open(os.path.join(sub_path, anno_file), "r") as f:
                        data = json.load(f)
                except:
                    continue
                
                fp = data["images"].get("facepart")
                bbox = data["images"].get("bbox")
                
                if fp not in FACE_PARTS or bbox is None:
                    continue
                
                if len(samples[fp]) < sample_limit:
                    img_path = os.path.join(img_root, EQU_MAPPING.get(equ, equ), sub, data["info"]["filename"])
                    if os.path.exists(img_path):
                        samples[fp].append((img_path, bbox))

    print("Processing samples...")
    final_rules = {}

    with get_landmarker() as landmarker:
        for fp, items in samples.items():
            print(f"Calibrating {FACE_PARTS[fp]}...")
            
            landmark_votes = defaultdict(int)
            ratios = []
            
            for img_path, bbox in tqdm(items):
                mp_image = mp.Image.create_from_file(img_path)
                result = landmarker.detect(mp_image)
                
                if not result.face_landmarks:
                    continue

                landmarks = result.face_landmarks[0]
                h, w = mp_image.height, mp_image.width
                lm_points = np.array([(lm.x * w, lm.y * h) for lm in landmarks])
                
                bx1, by1, bx2, by2 = bbox
                bcx, bcy = (bx1 + bx2) / 2, (by1 + by2) / 2
                
                dists = np.linalg.norm(lm_points - np.array([bcx, bcy]), axis=1)
                closest_idx = np.argmin(dists)
                landmark_votes[closest_idx] += 1
                
                face_width = np.max(lm_points[:, 0]) - np.min(lm_points[:, 0])
                crop_size = max(bx2 - bx1, by2 - by1) / 2
                
                if face_width > 0:
                    ratios.append(crop_size / face_width)

            if not landmark_votes:
                print(f"No valid samples for {FACE_PARTS[fp]}")
                continue

            best_lm = max(landmark_votes, key=landmark_votes.get)
            best_ratio = float(np.median(ratios))
            
            final_rules[fp] = {
                "name": FACE_PARTS[fp],
                "landmark_id": int(best_lm),
                "ratio": best_ratio
            }
            print(f" -> Landmark: {best_lm}, Ratio: {best_ratio:.4f}")

    output_path = os.path.join(RESOURCE_DIR, "calibration_rules.json")
    with open(output_path, "w") as f:
        json.dump(final_rules, f, indent=4)
    
    print(f"Saved to {output_path}")

if __name__ == "__main__":
    calculate_rules()
