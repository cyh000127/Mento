import torch
import timm
import os
import onnx
import numpy as np

# 설정: 변환할 모델 리스트
MODEL_CONFIGS = [
    # Regression Models
    {"name": "reg_moisture", "path": "checkpoint/regression/1st_coat/save_model/moisture/state_dict.bin", "nc": 1},
    {"name": "reg_pore", "path": "checkpoint/regression/1st_coat/save_model/pore/state_dict.bin", "nc": 1},
    {"name": "reg_pigmentation", "path": "checkpoint/regression/1st_coat/save_model/pigmentation/state_dict.bin", "nc": 1},
    {"name": "reg_wrinkle_eye", "path": "checkpoint/regression/1st_coat/save_model/wrinkle_Ra/state_dict.bin", "nc": 1},
    # Classification Models
    {"name": "class_wrinkle", "path": "checkpoint/class/1st_coat/save_model/wrinkle/state_dict.bin", "nc": 7},
    {"name": "class_sagging", "path": "checkpoint/class/1st_coat/save_model/sagging/state_dict.bin", "nc": 6},
]

MODEL_TYPE = "coatnet_2_rw_224"

def convert_to_fp32_onnx(config):
    ckpt_path = config["path"]
    if not os.path.exists(ckpt_path):
        print(f"[X] 파일을 찾을 수 없음: {ckpt_path}")
        return

    print(f"[*] 변환 시작 (FP32): {config['name']} ({ckpt_path})")
    
    # 1. PyTorch 모델 로드
    model = timm.create_model(MODEL_TYPE, pretrained=False, num_classes=config["nc"])
    checkpoint = torch.load(ckpt_path, map_location="cpu", weights_only=False)
    state_dict = checkpoint["model_state"] if "model_state" in checkpoint else checkpoint
    model.load_state_dict(state_dict)
    model.eval()

    # 2. ONNX Export (FP32)
    dummy_input = torch.randn(1, 3, 224, 224)
    base_path = os.path.dirname(ckpt_path)
    onnx_path = os.path.join(base_path, "model.onnx")

    torch.onnx.export(
        model, dummy_input, onnx_path,
        export_params=True, opset_version=14,
        do_constant_folding=True,
        input_names=['input'], output_names=['output']
    )

    print(f"[V] 완료: {onnx_path}")
    print("-" * 50)

if __name__ == "__main__":
    for config in MODEL_CONFIGS:
        convert_to_fp32_onnx(config)
