import os
import json
import cv2
import numpy as np
import argparse
import timm
import torch
import torch.nn.functional as F
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
from torchvision import transforms
from PIL import Image

os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"

# Constants
CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
RESOURCE_DIR = os.path.join(os.path.dirname(CURRENT_DIR), "resources")
RULES_PATH = os.path.join(RESOURCE_DIR, "calibration_rules.json")
MODEL_NAME = "coatnet_2_rw_224"
DEFAULT_RES = 224

# Face Parts Mapping (inverse of dataset)
PART_NAME_TO_ID = {
    "forehead": 1,
    "glabellus": 2,
    "l_perocular": 3,
    "r_perocular": 4,
    "l_cheek": 5,
    "lip": 7,
    "chin": 8
}

# Model Configuration (Hybrid)
# Type: "classification" (return class index) or "regression" (return float value)
MODEL_CONFIG = {
    "forehead": {"type": "classification", "path": None, "num_classes": 5},
    "glabellus": {"type": "classification", "path": None, "num_classes": 5},
    "l_perocular": {"type": "classification", "path": None, "num_classes": 5},
    "r_perocular": {"type": "classification", "path": None, "num_classes": 5},
    "l_cheek": {"type": "regression", "path": None, "num_classes": 1},
    "r_cheek": {"type": "regression", "path": None, "num_classes": 1},
    "lip": {"type": "regression", "path": None, "num_classes": 1},
    "chin": {"type": "regression", "path": None, "num_classes": 1}
}

def get_face_landmarker():
    base_options = python.BaseOptions(model_asset_path=os.path.join(RESOURCE_DIR, 'face_landmarker.task'))
    options = vision.FaceLandmarkerOptions(
        base_options=base_options,
        num_faces=1)
    return vision.FaceLandmarker.create_from_options(options)

class SkinAnalyzer:
    def __init__(self, model_path=None, device="cuda"):
        self.device = device
        self.rules = self.load_rules()
        self.models = self.load_models(model_path)
        self.landmarker = get_face_landmarker()
        
        self.transform = transforms.Compose([
            transforms.Resize((DEFAULT_RES, DEFAULT_RES), antialias=True),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])

    def load_rules(self):
        with open(RULES_PATH, "r") as f:
            return json.load(f)

    def load_models(self, model_root):
        """
        Loads specific models for each face part based on MODEL_CONFIG.
        """
        models = {}
        print(f"Initializing models on {self.device}...")
        
        for part, config in MODEL_CONFIG.items():
            # In real usage, use config["path"] if available
            # checkpoint_path = config["path"]
            
            # Create model architecture
            model = timm.create_model(
                MODEL_NAME, 
                pretrained=False, 
                num_classes=config["num_classes"]
            ).to(self.device).eval()
            
            # Load weights if path exists (commented out for prototype)
            # if checkpoint_path and os.path.exists(checkpoint_path):
            #     checkpoint = torch.load(checkpoint_path)
            #     model.load_state_dict(checkpoint)
            
            models[part] = model
            
        return models

    def predict(self, image_path):
        print(f"Processing {image_path}...")
        mp_image = mp.Image.create_from_file(image_path)
        
        detection_result = self.landmarker.detect(mp_image)
        if not detection_result.face_landmarks:
            print("No face detected.")
            return

        landmarks = detection_result.face_landmarks[0]
        h, w = mp_image.height, mp_image.width
        
        # Convert to numpy
        lm_points = np.array([(lm.x * w, lm.y * h) for lm in landmarks])
        face_width = np.max(lm_points[:, 0]) - np.min(lm_points[:, 0])
        
        img_cv = cv2.imread(image_path)
        img_rgb = cv2.cvtColor(img_cv, cv2.COLOR_BGR2RGB)
        
        results = {}
        
        # Sequential Inference Loop
        for part_id_str, rule in self.rules.items():
            part_name = rule["name"]
            
            # Skip if part not in config
            if part_name not in MODEL_CONFIG:
                continue
                
            config = MODEL_CONFIG[part_name]
            lm_id = rule["landmark_id"]
            ratio = rule["ratio"]
            
            # 1. Find Center
            center = lm_points[lm_id]
            cx, cy = int(center[0]), int(center[1])
            
            # 2. Calculate Size
            crop_half_size = int((ratio * face_width))
            
            # 3. Crop
            x1 = max(0, cx - crop_half_size)
            y1 = max(0, cy - crop_half_size)
            x2 = min(w, cx + crop_half_size)
            y2 = min(h, cy + crop_half_size)
            
            crop = img_rgb[y1:y2, x1:x2]
            
            if crop.size == 0:
                continue
                
            # 4. Preprocess
            pil_img = Image.fromarray(crop)
            tensor_img = self.transform(pil_img).unsqueeze(0).to(self.device)
            
            # 5. Inference (Using specific model for this part)
            model = self.models[part_name]
            
            with torch.no_grad():
                output = model(tensor_img)
                
                # Post-process based on model type
                if config["type"] == "classification":
                    # For classification, taking the argmax class
                    score = torch.argmax(output, dim=1).item()
                    # Optionally return probabilities: F.softmax(output, dim=1)
                elif config["type"] == "regression":
                    # For regression, taking the float value
                    score = output.item()
                else:
                    score = output.item()
            
            results[part_name] = {
                "score": score,
                "type": config["type"],
                "crop_coords": [x1, y1, x2, y2]
            }
            
            # Visualize
            cv2.rectangle(img_cv, (x1, y1), (x2, y2), (0, 255, 0), 2)
            cv2.putText(img_cv, f"{part_name}: {score:.2f}", (x1, y1-5), cv2.FONT_HERSHEY_SIMPLEX, 0.5, (0, 255, 0), 2)

        # Save visualization
        out_path = image_path.replace(".jpg", "_result.jpg")
        cv2.imwrite(out_path, img_cv)
        print(f"Saved result visual to {out_path}")
        return results

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--image", required=True, help="Path to input image")
    args = parser.parse_args()
    
    analyzer = SkinAnalyzer()
    results = analyzer.predict(args.image)
    print(json.dumps(results, indent=2))
