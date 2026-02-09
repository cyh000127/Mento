"""PyTorch-based skin analysis inference (legacy reference implementation)."""
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

os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
RESOURCE_DIR = os.path.join(os.path.dirname(os.path.dirname(CURRENT_DIR)), "app", "resources")
CHECKPOINT_ROOT = os.path.join(os.path.dirname(os.path.dirname(CURRENT_DIR)), "checkpoint")

MODEL_NAME = "coatnet_2_rw_224"
DEFAULT_RES = 224

MODELS = {
    "class_wrinkle": {"type": "classification", "path": "class/1st_coat/save_model/wrinkle/state_dict.bin", "nc": 7},
    "class_sagging": {"type": "classification", "path": "class/1st_coat/save_model/sagging/state_dict.bin", "nc": 6},
    "reg_pigmentation": {"type": "regression", "path": "regression/1st_coat/save_model/pigmentation/state_dict.bin", "nc": 1},
    "reg_pore": {"type": "regression", "path": "regression/1st_coat/save_model/pore/state_dict.bin", "nc": 1},
    "reg_moisture": {"type": "regression", "path": "regression/1st_coat/save_model/moisture/state_dict.bin", "nc": 1},
}

VIEW_RULES = {
    "front": {
        "forehead_wrinkle": {"model": "class_wrinkle", "rule": "forehead"},
        "glabellus_wrinkle": {"model": "class_wrinkle", "rule": "glabellus"},
        "pigmentation": {"model": "reg_pigmentation", "rule": "full_image"}
    },
    "l30": {"cheek_moisture": {"model": "reg_moisture", "rule": "l_cheek"}, "cheek_pore": {"model": "reg_pore", "rule": "l_cheek"}},
    "r30": {"cheek_moisture": {"model": "reg_moisture", "rule": "r_cheek"}, "cheek_pore": {"model": "reg_pore", "rule": "r_cheek"}}
}

def get_landmarker():
    base_options = python.BaseOptions(model_asset_path=os.path.join(RESOURCE_DIR, 'face_landmarker.task'))
    return vision.FaceLandmarker.create_from_options(vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1))

class SkinAnalyzer:
    def __init__(self, device="cuda"):
        self.device = device if torch.cuda.is_available() else "cpu"
        self.rules = self._load_rules()
        self.models = self._load_models()
        self.landmarker = get_landmarker()
        self.transform = transforms.Compose([
            transforms.Resize((DEFAULT_RES, DEFAULT_RES), antialias=True),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])

    def _load_rules(self):
        with open(os.path.join(RESOURCE_DIR, "calibration_rules.json"), "r") as f:
            return json.load(f)

    def _load_models(self):
        loaded = {}
        for key, cfg in MODELS.items():
            model = timm.create_model(MODEL_NAME, pretrained=False, num_classes=cfg["nc"]).to(self.device).eval()
            path = os.path.join(CHECKPOINT_ROOT, cfg["path"])
            if os.path.exists(path):
                ckpt = torch.load(path, map_location=self.device, weights_only=False)
                model.load_state_dict(ckpt.get("model_state", ckpt))
            loaded[key] = (model, cfg["type"])
        return loaded

    def _extract_crop(self, img_rgb, lm_points, face_width, rule_name):
        if rule_name == "full_image":
            return img_rgb
        for rval in self.rules.values():
            if rval["name"] == rule_name:
                cx, cy = lm_points[rval["landmark_id"]]
                half = int(rval["ratio"] * face_width)
                h, w = img_rgb.shape[:2]
                x1, y1 = max(0, int(cx - half)), max(0, int(cy - half))
                x2, y2 = min(w, int(cx + half)), min(h, int(cy + half))
                return img_rgb[y1:y2, x1:x2]
        return None

    def predict(self, front_path=None, l30_path=None, r30_path=None):
        results = {}
        for view, path in [("front", front_path), ("l30", l30_path), ("r30", r30_path)]:
            if path and os.path.exists(path):
                results[view] = self._process_view(path, view)
        return results

    def _process_view(self, image_path, view_type):
        if view_type not in VIEW_RULES:
            return {}

        mp_image = mp.Image.create_from_file(image_path)
        result = self.landmarker.detect(mp_image)
        if not result.face_landmarks:
            return {}

        lms = result.face_landmarks[0]
        h, w = mp_image.height, mp_image.width
        lm_points = np.array([(lm.x * w, lm.y * h) for lm in lms])
        face_width = np.max(lm_points[:, 0]) - np.min(lm_points[:, 0])
        
        img_rgb = cv2.cvtColor(cv2.imread(image_path), cv2.COLOR_BGR2RGB)
        view_results = {}

        for key, item in VIEW_RULES[view_type].items():
            rule_names = [item["rule"]] if isinstance(item["rule"], str) else item["rule"]
            scores = []
            
            for r_name in rule_names:
                crop = self._extract_crop(img_rgb, lm_points, face_width, r_name)
                if crop is None or crop.size == 0:
                    continue
                
                tensor = self.transform(Image.fromarray(crop)).unsqueeze(0).to(self.device)
                model, m_type = self.models[item["model"]]
                
                with torch.no_grad():
                    out = model(tensor)
                    val = torch.argmax(out, dim=1).item() if m_type == "classification" else out.item()
                    scores.append(val)
            
            if scores:
                view_results[key] = {"raw": float(sum(scores) / len(scores))}

        return view_results

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--front", help="Frontal image path")
    parser.add_argument("--l30", help="L30 image path")
    parser.add_argument("--r30", help="R30 image path")
    args = parser.parse_args()
    
    analyzer = SkinAnalyzer()
    results = analyzer.predict(args.front, args.l30, args.r30)
    print(json.dumps(results, indent=2))
