import os
import json
import cv2
import numpy as np
import argparse
import timm
import torch
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
from torchvision import transforms
from PIL import Image
from app.analysis.scoring import SkinScorer

os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
RESOURCE_DIR = os.path.join(os.path.dirname(CURRENT_DIR), "resources")
RULES_PATH = os.path.join(RESOURCE_DIR, "calibration_rules.json")
CHECKPOINT_ROOT = os.path.join(os.path.dirname(os.path.dirname(CURRENT_DIR)), "checkpoint")

MODEL_NAME = "coatnet_2_rw_224"
DEFAULT_RES = 224

# Mapping: Part -> {Type, CheckpointPath, NumClasses}
# Class: Dryness(5), Pigmentation(6), Pore(6), Sagging(6), Wrinkle(7)
# Reg: Pigmentation(1), Moisture(1), Elasticity(1), Wrinkle(1), Pore(1)

MODELS = {
    # Classification Models
    "class_wrinkle": {
        "type": "classification", 
        "path": os.path.join(CHECKPOINT_ROOT, "class/1st_coat/save_model/wrinkle/state_dict.bin"), 
        "num_classes": 7
    },
    "class_sagging": {
        "type": "classification", 
        "path": os.path.join(CHECKPOINT_ROOT, "class/1st_coat/save_model/sagging/state_dict.bin"), 
        "num_classes": 6
    },
    
    # Regression Models
    "reg_pigmentation": {
        "type": "regression", 
        "path": os.path.join(CHECKPOINT_ROOT, "regression/1st_coat/save_model/pigmentation/state_dict.bin"), 
        "num_classes": 1
    },
    "reg_pore": {
        "type": "regression", 
        "path": os.path.join(CHECKPOINT_ROOT, "regression/1st_coat/save_model/pore/state_dict.bin"), 
        "num_classes": 1
    },
    "reg_moisture": {
        "type": "regression", 
        "path": os.path.join(CHECKPOINT_ROOT, "regression/1st_coat/save_model/moisture/state_dict.bin"), 
        "num_classes": 1
    },
    "reg_elasticity": {
        "type": "regression", 
        "path": os.path.join(CHECKPOINT_ROOT, "regression/1st_coat/save_model/elasticity_R2/state_dict.bin"), 
        "num_classes": 1
    }
}

# (View, PartName) -> {ModelKey, RuleName(for cropping)}
VIEW_RULES = {
    "front": {
        "forehead_wrinkle": {"model": "class_wrinkle", "rule": "forehead"},
        "glabellus_wrinkle": {"model": "class_wrinkle", "rule": "glabellus"},
        "perocular_wrinkle": {"model": "class_wrinkle", "rule": ("l_perocular", "r_perocular")}, # Both eyes
        "pigmentation": {"model": "reg_pigmentation", "rule": "full_image"} # Global Pigmentation
    },
    "l30": {
        "cheek_moisture": {"model": "reg_moisture", "rule": "l_cheek"},
        "cheek_pore": {"model": "reg_pore", "rule": "l_cheek"},
        "cheek_elasticity": {"model": "reg_elasticity", "rule": "l_cheek"},
        # "perocular_wrinkle": {"model": "class_wrinkle", "rule": "l_perocular"}
    },
    "r30": {
        "cheek_moisture": {"model": "reg_moisture", "rule": "r_cheek"},
        "cheek_pore": {"model": "reg_pore", "rule": "r_cheek"},
        "cheek_elasticity": {"model": "reg_elasticity", "rule": "r_cheek"},
        # "perocular_wrinkle": {"model": "class_wrinkle", "rule": "r_perocular"}
    }
}

def get_face_landmarker():
    base_options = python.BaseOptions(model_asset_path=os.path.join(RESOURCE_DIR, 'face_landmarker.task'))
    options = vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
    return vision.FaceLandmarker.create_from_options(options)

class SkinAnalyzer:
    def __init__(self, device="cuda"):
        self.device = device if torch.cuda.is_available() else "cpu"
        self.rules = self.load_rules()
        self.models = self.load_models()
        self.landmarker = get_face_landmarker()
        
        self.transform = transforms.Compose([
            transforms.Resize((DEFAULT_RES, DEFAULT_RES), antialias=True),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])

        ])
        
        self.scorer = SkinScorer()

    def load_rules(self):
        with open(RULES_PATH, "r") as f:
            return json.load(f)

    def load_models(self):
        loaded_models = {}
        print(f"Initializing models on {self.device}...")
        
        for key, config in MODELS.items():
            print(f"Loading {key}...")
            model = timm.create_model(
                MODEL_NAME, 
                pretrained=False, 
                num_classes=config["num_classes"]
            ).to(self.device).eval()
            
            if os.path.exists(config["path"]):
                checkpoint = torch.load(config["path"], map_location=self.device, weights_only=False)
                if "model_state" in checkpoint:
                    state_dict = checkpoint["model_state"]
                else:
                    state_dict = checkpoint
                model.load_state_dict(state_dict)
            else:
                print(f"[WARNING] Checkpoint not found: {config['path']}")
            
            loaded_models[key] = (model, config["type"])
            
        return loaded_models

    def predict_multiview(self, front_path, l30_path, r30_path, age=None, gender=None):
        results = {}
        
        # Process Front
        if front_path and os.path.exists(front_path):
            results["front"] = self._process_view(front_path, "front", age, gender)
            
        # Process L30
        if l30_path and os.path.exists(l30_path):
            results["l30"] = self._process_view(l30_path, "l30", age, gender)
            
        # Process R30
        if r30_path and os.path.exists(r30_path):
            results["r30"] = self._process_view(r30_path, "r30", age, gender)
            
        return results

    def _process_view(self, image_path, view_type, age=None, gender=None):
        print(f"Processing {view_type} view: {image_path}")
        if view_type not in VIEW_RULES:
            return {}

        mp_image = mp.Image.create_from_file(image_path)
        detection_result = self.landmarker.detect(mp_image)
        
        if not detection_result.face_landmarks:
            print(f"No face detected in {view_type}.")
            return {}

        landmarks = detection_result.face_landmarks[0]
        h, w = mp_image.height, mp_image.width
        lm_points = np.array([(lm.x * w, lm.y * h) for lm in landmarks])
        face_width = np.max(lm_points[:, 0]) - np.min(lm_points[:, 0])
        
        img_cv = cv2.imread(image_path)
        img_rgb = cv2.cvtColor(img_cv, cv2.COLOR_BGR2RGB)
        
        view_results = {}
        config = VIEW_RULES[view_type]

        for result_key, item in config.items():
            model_key = item["model"]
            rule_names = item["rule"]
            
            if isinstance(rule_names, str):
                rule_names = [rule_names]
            
            scores = []
            
            for r_name in rule_names:
                # Handle Special Rule: full_image
                if r_name == "full_image":
                    crop = img_rgb
                else:
                    # Find rule in calibration_rules
                    # r_name matches "name" field in json
                    target_rule = None
                    for rid, rval in self.rules.items():
                        if rval["name"] == r_name:
                            target_rule = rval
                            break
                    
                    if not target_rule:
                        continue
                        
                    lm_id = target_rule["landmark_id"]
                    ratio = target_rule["ratio"]
                    
                    center = lm_points[lm_id]
                    cx, cy = int(center[0]), int(center[1])
                    crop_half = int((ratio * face_width))
                    
                    x1, y1 = max(0, cx - crop_half), max(0, cy - crop_half)
                    x2, y2 = min(w, cx + crop_half), min(h, cy + crop_half)
                    
                    crop = img_rgb[y1:y2, x1:x2]
                    cv2.rectangle(img_cv, (x1, y1), (x2, y2), (0, 255, 0), 2)

                if crop.size == 0: continue
                
                pil_img = Image.fromarray(crop)
                tensor_img = self.transform(pil_img).unsqueeze(0).to(self.device)
                
                model, m_type = self.models[model_key]
                
                with torch.no_grad():
                    out = model(tensor_img)
                    if m_type == "classification":
                        val = torch.argmax(out, dim=1).item()
                    else:
                        val = out.item()
                    scores.append(val)
                

                
                # Visual Debug
                # cv2.rectangle(img_cv, (x1, y1), (x2, y2), (0, 255, 0), 2) # Moved inside loop for crop logic
            
            if scores:
                avg_score = sum(scores) / len(scores)
                
                # Determine category for scoring
                score_category = None
                if "pigmentation" in result_key: score_category = "pigmentation"
                elif "pore" in result_key: score_category = "pore"
                elif "moisture" in result_key: score_category = "moisture"
                elif "elasticity" in result_key: score_category = "elasticity"
                elif "wrinkle" in result_key: score_category = "wrinkle"
                elif "sagging" in result_key: score_category = "sagging"
                
                
                final_score = 0
                real_value = avg_score
                
                if score_category:
                    # Get Denormalized Real Value first
                    real_value = self.scorer.get_real_value(score_category, avg_score)
                    # Calculate Score based on Real Value
                    # Note: scorer.calculate_score typically takes denormalized value?
                    # Wait, scorer.calculate_score calls get_real_value internally!
                    # Let's check scoring.py.
                    # calculate_score(cat, model_val) -> gets real -> gets percentile.
                    # So calling calculate_score with avg_score (normalized) is correct.
                    final_score = self.scorer.calculate_score(score_category, avg_score, age=age, gender=gender)
                
                # Return structure: {"raw": val, "score": val}
                # "raw" should be the Real Value for user readability.
                view_results[result_key] = {
                    "raw": float(real_value),
                    "score": int(final_score)
                }


        # Save debug image
        out_path = image_path.replace(".jpg", f"_{view_type}_result.jpg")
        cv2.imwrite(out_path, img_cv)
        
        return view_results

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--front", help="Path to Frontal image")
    parser.add_argument("--l30", help="Path to L30 image")
    parser.add_argument("--r30", help="Path to R30 image")
    parser.add_argument("--age", help="User Age", type=int, default=None)
    parser.add_argument("--gender", help="User Gender (male/female)", default=None)
    
    args = parser.parse_args()
    
    analyzer = SkinAnalyzer()
    
    # If single image passed to --front but meant for testing, handle it
    # For now strictly follow args
    
    results = analyzer.predict_multiview(args.front, args.l30, args.r30, args.age, args.gender)
    print(json.dumps(results, indent=2))
