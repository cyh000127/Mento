
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

# Use absolute paths for robustness
CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
# app/analysis -> app/resources
RESOURCE_DIR = os.path.join(os.path.dirname(CURRENT_DIR), "resources") 
RULES_PATH = os.path.join(RESOURCE_DIR, "calibration_rules.json")
# app/analysis -> app -> .. -> checkpoint
CHECKPOINT_ROOT = os.path.join(os.path.dirname(os.path.dirname(CURRENT_DIR)), "checkpoint")

MODEL_NAME = "coatnet_2_rw_224"
DEFAULT_RES = 224

class DataDrivenScorer:
    """
    Implements the Data-Driven Relative Scoring logic.
    Thresholds are based on the top 20%, 40%, 60%, 80% percentiles (Grade 1 to 5).
    Score mapping: 
       Grade 1 (Best) -> Score 0-20
       Grade 5 (Worst) -> Score 81-100
    """
    def __init__(self):
        # Format: "attribute": [p20, p40, p60, p80]
        # p20 is the boundary between Grade 5/4 or 1/2 depending on High/Low logic.
        
        # High Raw = Bad (Pore, Pigmentation, Wrinkle Ra)
        # Grade 1 ( < p20 ) -> Score 0-20
        # Grade 5 ( > p80 ) -> Score 81-100
        
        # High Raw = Good (Moisture)
        # Grade 1 ( > p80 ) -> Score 0-20 (Inverted Score)
        # Grade 5 ( < p20 ) -> Score 81-100
        
        self.thresholds = {
            "moisture": [52.7, 55.5, 58.5, 62.5], # Low is Bad
            "pore": [577.2, 817.7, 1007.0, 1250.0], # Interpolated p60/p80 based on distribution
            "pigmentation": [110.0, 135.0, 160.0, 198.0],
            "eye_wrinkle": [17.1, 18.9, 20.8, 23.3] 
        }
        
        # Description Templates (Grade 1 to 5)
        self.descriptions = {
            "moisture": [
                "수분이 가득 차오른 매우 촉촉한 상태입니다.", # Grade 1
                "수분 밸런스가 좋습니다. 현재 관리를 유지하세요.", # Grade 2
                "보통 수준이나, 환절기에는 건조할 수 있습니다.", # Grade 3
                "피부가 건조합니다. 수분 섭취와 보습제가 필요합니다.", # Grade 4
                "극심한 건조 상태입니다. 집중적인 수분 공급이 시급합니다." # Grade 5
            ],
            "pore": [
                "모공이 거의 보이지 않는 매끄러운 도자기 피부입니다.",
                "모공 관리가 잘 되어 피부결이 고른 편입니다.",
                "가까이서 보면 모공이 다소 보이는 수준입니다.",
                "나비존을 중심으로 모공 확장이 눈에 띕니다.",
                "모공 늘어짐이 도드라져, 피지 조절과 탄력 케어가 필요합니다."
            ],
            "wrinkle": [
                "주름 하나 없이 팽팽하고 매끈한 동안 피부입니다.",
                "잔주름이 거의 없고 탄력 있는 상태입니다.",
                "표정 주름과 미세한 잔주름이 생기기 시작했습니다.",
                "눈가와 팔자 부위의 주름이 깊어지고 있습니다.",
                "깊은 주름이 자리 잡고 있어 적극적인 안티에이징이 필요합니다."
            ],
            "pigmentation": [
                "잡티 없이 맑고 투명한 피부 톤입니다.",
                "비교적 깨끗하나 옅은 기미가 보일 수 있습니다.",
                "눈에 띄는 잡티와 색소 침착이 다소 있습니다.",
                "기미와 주근깨가 짙어져 칙칙해 보일 수 있습니다.",
                "전반적인 색소 침착으로 인해 미백 관리가 시급합니다."
            ],
            "sagging": [
                "페이스 라인이 무너짐 없이 날렵하고 탄탄합니다.",
                "처짐 없이 건강한 탄력을 유지하고 있습니다.",
                "턱 선이 살짝 둔해지며 탄력이 떨어지고 있습니다.",
                "페이스 라인이 흐트러지고 볼 처짐이 보입니다.",
                "피부 탄력 저하가 뚜렷하여 리프팅 관리를 추천합니다."
            ]
        }

    def _interpolate_score(self, val, min_v, max_v, score_min, score_max):
        """Linearly interpolates val between min_v and max_v to score_min/score_max."""
        if max_v == min_v: return score_min
        ratio = (val - min_v) / (max_v - min_v)
        return score_min + ratio * (score_max - score_min)

    def calculate(self, attribute, value):
        score = 0
        grade = 0
        
        # 1. Sagging (Class 0-5)
        if attribute == "sagging":
            cls = int(round(value))
            # Grade 1 (Good) -> Class 0
            # Grade 2 -> Class 1
            # Grade 3 -> Class 2
            # Grade 4 -> Class 3
            # Grade 5 -> Class 4, 5
            
            if cls == 0: grade = 1
            elif cls == 1: grade = 2
            elif cls == 2: grade = 3
            elif cls == 3: grade = 4
            else: grade = 5
            
            # Score mappings
            if grade == 1: score = 15
            elif grade == 2: score = 30
            elif grade == 3: score = 50
            elif grade == 4: score = 70
            else: score = 90
            
            return score, grade

        # 2. Continuous Attributes
        th = self.thresholds.get(attribute)
        if not th: return 0, 0
        
        p20, p40, p60, p80 = th
        
        if attribute == "moisture":
            # Higher Value = Better = Lower Score = Lower Grade
            # Value > p80 (Best) -> Grade 1
            if value > p80:
                grade = 1
                score = self._interpolate_score(value, p80, p80+10, 20, 0) # Decay to 0
            elif value > p60:
                grade = 2
                score = self._interpolate_score(value, p60, p80, 40, 21)
            elif value > p40:
                grade = 3
                score = self._interpolate_score(value, p40, p60, 60, 41)
            elif value > p20:
                grade = 4
                score = self._interpolate_score(value, p20, p40, 80, 61)
            else:
                grade = 5
                score = self._interpolate_score(value, p20-10, p20, 100, 81)
                
        else: # Pore, Pigmentation, Eye Wrinkle (Higher = Worse = Higher Score)
            # Value < p20 (Best) -> Grade 1
            if value < p20:
                grade = 1
                score = self._interpolate_score(value, 0, p20, 0, 20)
            elif value < p40:
                grade = 2
                score = self._interpolate_score(value, p20, p40, 21, 40)
            elif value < p60:
                grade = 3
                score = self._interpolate_score(value, p40, p60, 41, 60)
            elif value < p80:
                grade = 4
                score = self._interpolate_score(value, p60, p80, 61, 80)
            else:
                grade = 5
                score = self._interpolate_score(value, p80, p80*1.5, 81, 100) # Cap at 100
                
        return min(max(int(score), 0), 100), grade

    def get_description(self, attribute, grade):
        return self.descriptions.get(attribute, [""]*5)[grade-1]

class ServiceAnalyzer:
    def __init__(self, device="cuda"):
        self.device = device if torch.cuda.is_available() else "cpu"
        self.rules = self._load_json(RULES_PATH)
        self.landmarker = self._init_landmarker()
        self.scorer = DataDrivenScorer()
        
        self.transform = transforms.Compose([
            transforms.Resize((DEFAULT_RES, DEFAULT_RES), antialias=True),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])
        
        self.models = self._load_models()

    def _load_json(self, path):
        with open(path, "r") as f: return json.load(f)
        
    def _init_landmarker(self):
        base_options = python.BaseOptions(model_asset_path=os.path.join(RESOURCE_DIR, 'face_landmarker.task'))
        options = vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
        return vision.FaceLandmarker.create_from_options(options)

    def _load_models(self):
        # Define necessary models based on proposal
        # Moisture (Reg), Pore (Reg), Pigmentation (Reg), Wrinkle Eye (Reg), Wrinkle Forehead/Glab/Sagging (Class)
        
        configs = {
            "reg_moisture": {"type": "reg", "path": "regression/1st_coat/save_model/moisture/state_dict.bin", "nc": 1},
            "reg_pore": {"type": "reg", "path": "regression/1st_coat/save_model/pore/state_dict.bin", "nc": 1},
            "reg_pigmentation": {"type": "reg", "path": "regression/1st_coat/save_model/pigmentation/state_dict.bin", "nc": 1},
            "reg_wrinkle_eye": {"type": "reg", "path": "regression/1st_coat/save_model/wrinkle_Ra/state_dict.bin", "nc": 1},
            "class_wrinkle": {"type": "class", "path": "class/1st_coat/save_model/wrinkle/state_dict.bin", "nc": 7}, # Forehead/Glab
            "class_sagging": {"type": "class", "path": "class/1st_coat/save_model/sagging/state_dict.bin", "nc": 6},
        }
        
        loaded = {}
        print(f"Loading models on {self.device}...")
        for name, cfg in configs.items():
            model = timm.create_model(MODEL_NAME, pretrained=False, num_classes=cfg['nc']).to(self.device).eval()
            path = os.path.join(CHECKPOINT_ROOT, cfg['path'])
            if os.path.exists(path):
                ckpt = torch.load(path, map_location=self.device, weights_only=False)
                state = ckpt['model_state'] if 'model_state' in ckpt else ckpt
                model.load_state_dict(state)
                loaded[name] = (model, cfg['type'])
            else:
                print(f"[ERROR] Model missing: {path}")
        return loaded

    def _extract_crop(self, image_path, rule_name):
        if not os.path.exists(image_path): return None
        
        # 1. Detect Landmarks
        mp_img = mp.Image.create_from_file(image_path)
        res = self.landmarker.detect(mp_img)
        if not res.face_landmarks: return None
        
        lms = res.face_landmarks[0]
        h, w = mp_img.height, mp_img.width
        points = np.array([(lm.x * w, lm.y * h) for lm in lms])
        face_width = np.max(points[:,0]) - np.min(points[:,0])
        
        # 2. Find Rule
        target_rule = next((r for r in self.rules.values() if r["name"] == rule_name), None)
        
        img_bgr = cv2.imread(image_path)
        img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)
        
        if rule_name == "full_image" or not target_rule:
             # Fallback for sagging if no rule? or Pigmentation?
             # Just return full image resized? 
             # Ideally center crop face? Let's use full image for now if requested.
             return img_rgb
             
        # 3. Crop
        lm_id = target_rule["landmark_id"]
        ratio = target_rule["ratio"]
        cx, cy = points[lm_id]
        half = int(ratio * face_width)
        
        x1, y1 = int(max(0, cx - half)), int(max(0, cy - half))
        x2, y2 = int(min(w, cx + half)), int(min(h, cy + half))
        
        return img_rgb[y1:y2, x1:x2]

    def _run_inference(self, model_key, crop_img):
        if crop_img is None:
            # print(f"[DEBUG] Crop is None for {model_key}")
            return None
        
        if crop_img.size == 0 or model_key not in self.models: return None
        
        model, m_type = self.models[model_key]
        pil_img = Image.fromarray(crop_img)
        t_img = self.transform(pil_img).unsqueeze(0).to(self.device)
        
        with torch.no_grad():
            out = model(t_img)
            if m_type == "class":
                return torch.argmax(out, dim=1).item()
            else:
                 val = out.item()
                 # Apply Scalers (Denormalization)
                 # derived from scoring.py and max values
                 scaler = 1.0
                 if "moisture" in model_key: scaler = 100.0
                 elif "pore" in model_key: scaler = 2600.0
                 elif "pigmentation" in model_key: scaler = 350.0
                 elif "wrinkle_eye" in model_key: scaler = 50.0 # Assumption based on Ra max 50
                 
                 return val * scaler

    def analyze(self, front, l30, r30):
        # Debug: Check images exist
        # print(f"Analyzing: F={front}, L={l30}, R={r30}")
        
        # 1. MOISTURE (L30/R30 avg, Rule: cheek)
        m_l = self._run_inference("reg_moisture", self._extract_crop(l30, "l_cheek"))
        m_r = self._run_inference("reg_moisture", self._extract_crop(r30, "r_cheek"))
        val_moist = np.mean([v for v in [m_l, m_r] if v is not None]) if (m_l is not None or m_r is not None) else 0
        
        # 2. PORE (L30/R30 avg, Rule: cheek)
        p_l = self._run_inference("reg_pore", self._extract_crop(l30, "l_cheek"))
        p_r = self._run_inference("reg_pore", self._extract_crop(r30, "r_cheek"))
        val_pore = np.mean([v for v in [p_l, p_r] if v is not None]) if (p_l is not None or p_r is not None) else 0
        
        # 3. PIGMENTATION (L30/R30 avg, Rule: cheek - assuming cheek covers spots)
        pig_l = self._run_inference("reg_pigmentation", self._extract_crop(l30, "l_cheek"))
        pig_r = self._run_inference("reg_pigmentation", self._extract_crop(r30, "r_cheek"))
        val_pig = np.mean([v for v in [pig_l, pig_r] if v is not None]) if (pig_l is not None or pig_r is not None) else 0
        
        # 4. WRINKLE (Eye Reg R30 + Forehead Class F + Glabellus Class F)
        # Eye: R30, perocular
        w_eye = self._run_inference("reg_wrinkle_eye", self._extract_crop(r30, "r_perocular"))
        if w_eye is None: w_eye = 20.0
        
        # Forehead/Glab: Front
        w_fh = self._run_inference("class_wrinkle", self._extract_crop(front, "forehead"))
        if w_fh is None: w_fh = 0
        
        w_gl = self._run_inference("class_wrinkle", self._extract_crop(front, "glabellus"))
        if w_gl is None: w_gl = 0

        
        # WRINKLE Composite Logic
        # Eye Score
        s_eye, _ = self.scorer.calculate("eye_wrinkle", w_eye)
        # Class Scores
        s_fh = w_fh * 20
        s_gl = w_gl * 20
        
        score_wrinkle = int((s_eye + s_fh + s_gl) / 3)
        # Calculate grade for Wrinkle based on final score? 
        # Or Just composite? Let's use final score mapping for Grade or simply percentile of final score logic?
        # Proposal said: "Eye Score + Forehead + Glab".
        # Let's map final score back to grade for consistency.
        # Simple Logic: 0-20 -> G1, 80-100 -> G5
        grade_wrinkle = 1
        if score_wrinkle > 80: grade_wrinkle = 5
        elif score_wrinkle > 60: grade_wrinkle = 4
        elif score_wrinkle > 40: grade_wrinkle = 3
        elif score_wrinkle > 20: grade_wrinkle = 2
        
        # 5. SAGGING (Class R30, Full Image or Cheek? Let's use Full Image of R30)
        # R30 Full Image
        val_sag = self._run_inference("class_sagging", self._extract_crop(r30, "full_image")) or 0 # Class 0-5
        score_sag, grade_sag = self.scorer.calculate("sagging", val_sag)
        
        # -- Scorer Calc for Others --
        score_moist, grade_moist = self.scorer.calculate("moisture", val_moist)
        score_pore, grade_pore = self.scorer.calculate("pore", val_pore)
        score_pig, grade_pig = self.scorer.calculate("pigmentation", val_pig)
        
        # -- Total Score --
        # (Moist + Pore + Pig + Wrinkle*1.2 + Sag*1.2) / 5.4
        total_score_val = (score_moist + score_pore + score_pig + score_wrinkle*1.2 + score_sag*1.2) / 5.4
        total_score = int(total_score_val)
        
        # Total Grade
        if total_score <= 20: total_grade = 1
        elif total_score <= 40: total_grade = 2
        elif total_score <= 60: total_grade = 3
        elif total_score <= 80: total_grade = 4
        else: total_grade = 5
        
        # Skin Type Summary (Rule: Moisture(Dry) + Pore(Oil))
        # Moisture > 60 (Dry), Pore > 60 (Oily)
        # Wait, Moisture Score High = Dry.
        # Pore Score High = Oily (Large Pores).
        
        is_dry = score_moist >= 55 # Grade 3 mid point
        is_oily = score_pore >= 50 # Grade 3 mid point
        
        if is_dry and is_oily: skin_type = "수분 부족형 지성 (수부지)"
        elif is_dry and not is_oily: skin_type = "건성"
        elif not is_dry and is_oily: skin_type = "지성"
        else: skin_type = "중성 (이상적)"
        
        # Construct JSON
        result = {
            "status_code": 200,
            "message": "Analysis completed successfully",
            "data": {
                "total_score": total_score,
                "total_grade": total_grade,
                "skin_type_summary": skin_type,
                "details": {
                    "moisture": {
                        "score": score_moist, "grade": grade_moist, "raw_value": round(val_moist, 1),
                        "description": self.scorer.get_description("moisture", grade_moist)
                    },
                    "pore": {
                        "score": score_pore, "grade": grade_pore, "raw_value": int(val_pore),
                        "description": self.scorer.get_description("pore", grade_pore)
                    },
                    "wrinkle": {
                        "score": score_wrinkle, "grade": grade_wrinkle, "raw_value": round(w_eye, 1), # Only showing Eye Ra as raw
                        "description": self.scorer.get_description("wrinkle", grade_wrinkle)
                    },
                    "pigmentation": {
                        "score": score_pig, "grade": grade_pig, "raw_value": int(val_pig),
                        "description": self.scorer.get_description("pigmentation", grade_pig)
                    },
                    "sagging": {
                        "score": score_sag, "grade": grade_sag, "raw_value": int(val_sag),
                        "description": self.scorer.get_description("sagging", grade_sag)
                    }
                }
            }
        }
        return result

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--front", required=True)
    parser.add_argument("--l30", required=True)
    parser.add_argument("--r30", required=True)
    args = parser.parse_args()
    
    analyzer = ServiceAnalyzer()
    final_json = analyzer.analyze(args.front, args.l30, args.r30)
    print(json.dumps(final_json, indent=2, ensure_ascii=False))
