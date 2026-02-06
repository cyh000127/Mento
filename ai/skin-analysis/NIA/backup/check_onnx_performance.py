import torch
import timm
import onnxruntime as ort
import numpy as np
import time
import os

# 1. 경로 설정
MODEL_TYPE = "coatnet_2_rw_224"
PYTORCH_PATH = "checkpoint/regression/1st_coat/save_model/moisture/state_dict.bin"
ONNX_PATH = "checkpoint/regression/1st_coat/save_model/moisture/model.onnx"
NUM_CLASSES = 1

def benchmark():
    # 가상 입력 생성 (1, 3, 224, 224)
    dummy_input = np.random.randn(1, 3, 224, 224).astype(np.float32)
    dummy_input_torch = torch.from_numpy(dummy_input)

    # --- PyTorch 모델 로드 및 추론 ---
    print("[*] PyTorch 모델 로딩 중...")
    pt_model = timm.create_model(MODEL_TYPE, pretrained=False, num_classes=NUM_CLASSES)
    checkpoint = torch.load(PYTORCH_PATH, map_location="cpu", weights_only=False)
    pt_model.load_state_dict(checkpoint["model_state"] if "model_state" in checkpoint else checkpoint)
    pt_model.eval()

    # PyTorch 속도 측정 (10회 반복 후 평균)
    print("[*] PyTorch 추론 테스트 중...")
    start_pt = time.time()
    with torch.no_grad():
        for _ in range(10):
            pt_out = pt_model(dummy_input_torch).numpy()
    end_pt = (time.time() - start_pt) / 10
    print(f"    PyTorch 평균 추론 시간: {end_pt*1000:.2f}ms")

    # --- ONNX 모델 로드 및 추론 ---
    print("[*] ONNX 모델 로딩 중...")
    # CPU 최적화 설정
    sess_options = ort.SessionOptions()
    sess_options.graph_optimization_level = ort.GraphOptimizationLevel.ORT_ENABLE_ALL
    
    # ONNX 세션 생성
    ort_session = ort.InferenceSession(ONNX_PATH, sess_options, providers=['CPUExecutionProvider'])
    input_name = ort_session.get_inputs()[0].name

    # ONNX 속도 측정 (10회 반복 후 평균)
    print("[*] ONNX 추론 테스트 중...")
    start_onnx = time.time()
    for _ in range(10):
        onnx_out = ort_session.run(None, {input_name: dummy_input}) # FP32 입력으로 변경
    end_onnx = (time.time() - start_onnx) / 10
    print(f"    ONNX 평균 추론 시간: {end_onnx*1000:.2f}ms")

    # --- 결과 비교 ---
    # ONNX out is a list of outputs
    onnx_out_array = onnx_out[0]
    diff = np.abs(pt_out - onnx_out_array.astype(np.float32))
    print("[결과 분석]")
    print(f"▶ 속도 향상: {end_pt / end_onnx:.2f}배 빨라짐" if end_pt > end_onnx else f"▶ 속도 향상: ONNX가 {end_onnx / end_pt:.2f}배 느림 (CPU 특성)")
    print(f"▶ 최대 오차(Max Absolute Error): {np.max(diff):.6f}")
    
    if np.max(diff) < 1e-2:
        print("▶ 정확도 검증: [PASS] (FP16 변환 허용 범위 이내)")
    else:
        print("▶ 정확도 검증: [주의] 오차가 다소 큼")

    # 용량 비교
    pt_size = os.path.getsize(PYTORCH_PATH) / (1024*1024)
    onnx_size = os.path.getsize(ONNX_PATH) / (1024*1024)
    print(f"▶ 용량 변화: {pt_size:.1f}MB -> {onnx_size:.1f}MB")

if __name__ == "__main__":
    benchmark()
