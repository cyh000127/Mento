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
    1: "forehead",
    2: "glabellus",
    3: "l_perocular",
    4: "r_perocular",
    5: "l_cheek",
    6: "r_cheek",
    7: "lip",
    8: "chin"
}

EQU_MAPPING = {
    "01": "digital_camera",
    "02": "smart_pad",
    "03": "smart_phone"
}

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
RESOURCE_DIR = os.path.join(os.path.dirname(CURRENT_DIR), "resources")

def get_face_landmarker_options():
    base_options = python.BaseOptions(model_asset_path=os.path.join(RESOURCE_DIR, 'face_landmarker.task'))
    options = vision.FaceLandmarkerOptions(
        base_options=base_options,
        num_faces=1)
    return options

def calculate_rules(dataset_root="dataset", sample_limit=100):
    """
    Learns the best landmark ID and crop ratio for each face part.
    """
    label_root = os.path.join(dataset_root, "label")
    img_root = os.path.join(dataset_root, "img")
    
    # Stores (landmark_id, ratio) for each facepart
    stats = defaultdict(list) 
    
    # Collect samples
    samples = defaultdict(list)
    
    print("Collecting samples...")
    for equ in os.listdir(label_root):
        equ_path = os.path.join(label_root, equ)
        if not os.path.isdir(equ_path): continue
        
        for sub in os.listdir(equ_path):
            sub_path = os.path.join(equ_path, sub)
            if not os.path.isdir(sub_path): continue

            for anno_file in os.listdir(sub_path):
                if not anno_file.endswith(".json"): continue
                
                json_path = os.path.join(sub_path, anno_file)
                try:
                    with open(json_path, "r") as f:
                        data = json.load(f)
                except:
                    continue
                
                fp = data["images"].get("facepart")
                bbox = data["images"].get("bbox")
                
                if fp not in FACE_PARTS or bbox is None:
                    continue
                
                if len(samples[fp]) < sample_limit:
                    device_folder = EQU_MAPPING.get(equ, equ)
                    img_path = os.path.join(img_root, device_folder, sub, data["info"]["filename"])
                    if os.path.exists(img_path):
                        samples[fp].append((img_path, bbox))

    print("Processing samples using FaceLandmarker...")
    final_rules = {}
    options = get_face_landmarker_options()

    with vision.FaceLandmarker.create_from_options(options) as landmarker:
        for fp, items in samples.items():
            print(f"Calibrating FacePart {fp} ({FACE_PARTS[fp]})...")
            
            landmark_votes = defaultdict(int)
            ratios = []
            
            for img_path, bbox in tqdm(items):
                # Load image using MediaPipe syntax
                mp_image = mp.Image.create_from_file(img_path)
                
                detection_result = landmarker.detect(mp_image)
                
                if not detection_result.face_landmarks:
                    continue

                # Get first face
                landmarks = detection_result.face_landmarks[0]
                h, w = mp_image.height, mp_image.width

                # Convert normalized landmarks to pixel coordinates
                lm_points = np.array([(lm.x * w, lm.y * h) for lm in landmarks])
                
                # Ground Truth Center
                bx1, by1, bx2, by2 = bbox
                bcx, bcy = (bx1 + bx2) / 2, (by1 + by2) / 2
                
                # Find closest landmark to bbox center
                dists = np.linalg.norm(lm_points - np.array([bcx, bcy]), axis=1)
                closest_idx = np.argmin(dists)
                
                landmark_votes[closest_idx] += 1
                
                # Calculate Face Width
                face_width = np.max(lm_points[:, 0]) - np.min(lm_points[:, 0])
                
                bbox_width = bx2 - bx1
                bbox_height = by2 - by1
                crop_size = max(bbox_width, bbox_height) / 2
                
                if face_width > 0:
                    ratios.append(crop_size / face_width)

            if not landmark_votes:
                print(f"Warning: No valid samples for FacePart {fp}")
                continue

            best_lm = max(landmark_votes, key=landmark_votes.get)
            best_ratio = float(np.median(ratios))
            
            final_rules[fp] = {
                "name": FACE_PARTS[fp],
                "landmark_id": int(best_lm),
                "ratio": best_ratio
            }
            print(f" -> Best Landmark: {best_lm}, Ratio: {best_ratio:.4f}")

    # Save Rules
    with open(os.path.join(RESOURCE_DIR, "calibration_rules.json"), "w") as f:
        json.dump(final_rules, f, indent=4)
    
    print(f"Calibration complete. Rules saved to {os.path.join(RESOURCE_DIR, 'calibration_rules.json')}")

if __name__ == "__main__":
    calculate_rules()
