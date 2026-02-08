import os
import json
import cv2
import numpy as np
import onnxruntime as ort
import mediapipe as mp
from mediapipe.tasks import python
from mediapipe.tasks.python import vision
from PIL import Image

CURRENT_DIR = os.path.dirname(os.path.abspath(__file__))
RESOURCE_DIR = os.path.join(os.path.dirname(CURRENT_DIR), "resources") 
RULES_PATH = os.path.join(RESOURCE_DIR, "calibration_rules.json")
CHECKPOINT_ROOT = os.path.join(os.path.dirname(os.path.dirname(CURRENT_DIR)), "checkpoint")

DEFAULT_RES = 224

class DataDrivenScorer:
    def __init__(self):
        self.thresholds = {
            "moisture": [56.8, 60.3, 63.3, 67.2],
            "pore": [564.7, 792.8, 1008.3, 1243.7],
            "pigmentation": [104.5, 142.0, 168.1, 210.6],
            "eye_wrinkle": [18.3, 20.3, 22.2, 24.9] 
        }
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
        if max_v == min_v: return score_min
        ratio = (val - min_v) / (max_v - min_v)
        return score_min + ratio * (score_max - score_min)

    def calculate(self, attribute, value):
        score, grade = 0, 0
        
        valid_ranges = {
            "moisture": (30.0, 90.0),      # Reasonable moisture range
            "pore": (100.0, 3000.0),       # Reasonable pore area range
            "pigmentation": (30.0, 400.0), # Reasonable pigmentation range
            "eye_wrinkle": (10.0, 40.0),   # Reasonable Ra range
        }
        if attribute in valid_ranges:
            min_val, max_val = valid_ranges[attribute]
            value = max(min_val, min(value, max_val))
        
        if attribute == "sagging":
            cls = int(round(value))
            cls = max(0, min(cls, 5))  # Clamp to valid class range
            grade = min(cls + 1, 5) if cls < 4 else 5
            scores = [15, 30, 50, 70, 90]
            score = scores[grade-1]
            return score, grade

        th = self.thresholds.get(attribute)
        if not th: return 0, 0
        p20, p40, p60, p80 = th
        
        if attribute == "moisture":
            if value > p80: grade, score = 1, self._interpolate_score(value, p80, p80+10, 20, 0)
            elif value > p60: grade, score = 2, self._interpolate_score(value, p60, p80, 40, 21)
            elif value > p40: grade, score = 3, self._interpolate_score(value, p40, p60, 60, 41)
            elif value > p20: grade, score = 4, self._interpolate_score(value, p20, p40, 80, 61)
            else: grade, score = 5, self._interpolate_score(value, p20-10, p20, 100, 81)
        else:
            if value < p20: grade, score = 1, self._interpolate_score(value, 0, p20, 0, 20)
            elif value < p40: grade, score = 2, self._interpolate_score(value, p20, p40, 21, 40)
            elif value < p60: grade, score = 3, self._interpolate_score(value, p40, p60, 41, 60)
            elif value < p80: grade, score = 4, self._interpolate_score(value, p60, p80, 61, 80)
            else: grade, score = 5, self._interpolate_score(value, p80, p80*1.5, 81, 100)
                
        return min(max(int(score), 0), 100), grade

    def get_description(self, attribute, grade):
        return self.descriptions.get(attribute, [""]*5)[grade-1]

class ServiceAnalyzer:
    def __init__(self, device="cpu"):
        self.rules = self._load_json(RULES_PATH)
        self.landmarker = self._init_landmarker()
        self.scorer = DataDrivenScorer()
        
        self.sess_options = ort.SessionOptions()
        self.sess_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_ALL
        self.sess_options.enable_cpu_mem_arena = False
        
        num_threads = int(os.environ.get("ONNX_NUM_THREADS", os.environ.get("OMP_NUM_THREADS", "2")))
        self.sess_options.intra_op_num_threads = num_threads
        
        self.configs = {
            "reg_moisture": "regression/1st_robust/save_model/moisture/model.onnx",
            "reg_pore": "regression/1st_robust/save_model/pore/model.onnx",
            "reg_pigmentation": "regression/1st_robust/save_model/pigmentation/model.onnx",
            "reg_wrinkle_eye": "regression/1st_robust/save_model/wrinkle_Ra/model.onnx",
            "class_wrinkle": "class/1st_robust/save_model/wrinkle/model.onnx",
            "class_sagging": "class/1st_robust/save_model/sagging/model.onnx",
        }
        self.sessions = self._load_sessions()

    def _load_json(self, path):
        with open(path, "r") as f: return json.load(f)
        
    def _init_landmarker(self):
        base_options = python.BaseOptions(model_asset_path=os.path.join(RESOURCE_DIR, 'face_landmarker.task'))
        options = vision.FaceLandmarkerOptions(base_options=base_options, num_faces=1)
        return vision.FaceLandmarker.create_from_options(options)

    def _load_sessions(self):
        sessions = {}
        print("Loading ONNX models...")
        for name, rel_path in self.configs.items():
            full_path = os.path.join(CHECKPOINT_ROOT, rel_path)
            if os.path.exists(full_path):
                sessions[name] = ort.InferenceSession(full_path, self.sess_options, providers=['CPUExecutionProvider'])
            else:
                print(f"[ERROR] ONNX Model missing: {full_path}")
        return sessions

    def _preprocess(self, crop_img):
        img = cv2.resize(crop_img, (DEFAULT_RES, DEFAULT_RES))
        img = img.astype(np.float32) / 255.0
        
        mean = np.array([0.485, 0.456, 0.406], dtype=np.float32)
        std = np.array([0.229, 0.224, 0.225], dtype=np.float32)
        img = (img - mean) / std
        img = img.transpose(2, 0, 1)[np.newaxis, ...]
        return img

    def _process_image(self, image_path):
        if not os.path.exists(image_path): return None, None
        img_bgr = cv2.imread(image_path)
        if img_bgr is None: return None, None
        img_rgb = cv2.cvtColor(img_bgr, cv2.COLOR_BGR2RGB)
        mp_img = mp.Image(image_format=mp.ImageFormat.SRGB, data=img_rgb)
        res = self.landmarker.detect(mp_img)
        return img_rgb, res

    def _extract_crop(self, img_rgb, detection_result, rule_name):
        if img_rgb is None: return None
        
        if not detection_result or not detection_result.face_landmarks:
            return img_rgb if rule_name == "full_image" else None

        lms, h, w = detection_result.face_landmarks[0], img_rgb.shape[0], img_rgb.shape[1]
        points = np.array([(lm.x * w, lm.y * h) for lm in lms])
        face_width = np.max(points[:,0]) - np.min(points[:,0])
        
        target_rule = next((r for r in self.rules.values() if r["name"] == rule_name), None)
        
        if rule_name == "full_image" or not target_rule: return img_rgb
        
        lm_id, ratio = target_rule["landmark_id"], target_rule["ratio"]
        cx, cy = points[lm_id]
        half = int(ratio * face_width)
        
        x1, y1 = int(max(0, cx - half)), int(max(0, cy - half))
        x2, y2 = int(min(w, cx + half)), int(min(h, cy + half))
        
        return img_rgb[y1:y2, x1:x2]

    def _run_inference(self, model_key, crop_img):
        if crop_img is None or model_key not in self.sessions: return None
        input_data = self._preprocess(crop_img)
        session = self.sessions[model_key]
        out = session.run(None, {session.get_inputs()[0].name: input_data})[0]
        
        if "class" in model_key:
            return int(np.argmax(out, axis=1).item())
        else:
            val = float(out.item())
            scaler = {"moisture": 100.0, "pore": 2600.0, "pigmentation": 350.0, "wrinkle_eye": 50.0}
            for k, s in scaler.items():
                if k in model_key: return val * s
            return val

    def analyze(self, front, l30, r30):
        img_f, res_f = self._process_image(front)
        img_l, res_l = self._process_image(l30)
        img_r, res_r = self._process_image(r30)

        m_l = self._run_inference("reg_moisture", self._extract_crop(img_l, res_l, "l_cheek"))
        m_r = self._run_inference("reg_moisture", self._extract_crop(img_r, res_r, "r_cheek"))
        val_moist = np.mean([v for v in [m_l, m_r] if v is not None]) if (m_l is not None or m_r is not None) else 0
        
        p_l = self._run_inference("reg_pore", self._extract_crop(img_l, res_l, "l_cheek"))
        p_r = self._run_inference("reg_pore", self._extract_crop(img_r, res_r, "r_cheek"))
        val_pore = np.mean([v for v in [p_l, p_r] if v is not None]) if (p_l is not None or p_r is not None) else 0
        
        pig_l = self._run_inference("reg_pigmentation", self._extract_crop(img_l, res_l, "l_cheek"))
        pig_r = self._run_inference("reg_pigmentation", self._extract_crop(img_r, res_r, "r_cheek"))
        val_pig = np.mean([v for v in [pig_l, pig_r] if v is not None]) if (pig_l is not None or pig_r is not None) else 0
        
        w_eye = self._run_inference("reg_wrinkle_eye", self._extract_crop(img_r, res_r, "r_perocular")) or 20.0
        w_fh = self._run_inference("class_wrinkle", self._extract_crop(img_f, res_f, "forehead")) or 0
        w_gl = self._run_inference("class_wrinkle", self._extract_crop(img_f, res_f, "glabellus")) or 0
        
        s_eye, _ = self.scorer.calculate("eye_wrinkle", w_eye)
        score_fh = min(w_fh * (100 / 6), 100)
        score_gl = min(w_gl * (100 / 6), 100)
        score_wrinkle = int((s_eye + score_fh + score_gl) / 3)
        grade_wrinkle = min(int(score_wrinkle/20)+1, 5)
        
        val_sag = self._run_inference("class_sagging", self._extract_crop(img_r, res_r, "full_image")) or 0
        score_sag, grade_sag = self.scorer.calculate("sagging", val_sag)
        score_moist, grade_moist = self.scorer.calculate("moisture", val_moist)
        score_pore, grade_pore = self.scorer.calculate("pore", val_pore)
        score_pig, grade_pig = self.scorer.calculate("pigmentation", val_pig)
        
        total_score = int((score_moist + score_pore + score_pig + score_wrinkle*1.2 + score_sag*1.2) / 5.4)
        total_grade = min(int(total_score/20)+1, 5)
        
        skin_type = "중성"
        if score_moist >= 55 and score_pore >= 50: skin_type = "수분 부족형 지성"
        elif score_moist >= 55: skin_type = "건성"
        elif score_pore >= 50: skin_type = "지성"

        return {
            "status_code": 200, "message": "Success",
            "data": {
                "total_score": total_score, "total_grade": total_grade, "skin_type_summary": skin_type,
                "details": {
                    "moisture": {"score": score_moist, "grade": grade_moist, "description": self.scorer.get_description("moisture", grade_moist)},
                    "pore": {"score": score_pore, "grade": grade_pore, "description": self.scorer.get_description("pore", grade_pore)},
                    "wrinkle": {"score": score_wrinkle, "grade": grade_wrinkle, "description": self.scorer.get_description("wrinkle", grade_wrinkle)},
                    "pigmentation": {"score": score_pig, "grade": grade_pig, "description": self.scorer.get_description("pigmentation", grade_pig)},
                    "sagging": {"score": score_sag, "grade": grade_sag, "description": self.scorer.get_description("sagging", grade_sag)}
                }
            }
        }