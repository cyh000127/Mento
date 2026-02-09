import torch
import timm
import os
import onnx
import numpy as np
import subprocess

MODEL_CONFIGS = [
    # Regression Models
    {"name": "reg_moisture", "path": "checkpoint/regression/1st_robust/save_model/moisture/state_dict.bin", "nc": 1},
    {"name": "reg_pore", "path": "checkpoint/regression/1st_robust/save_model/pore/state_dict.bin", "nc": 1},
    {"name": "reg_pigmentation", "path": "checkpoint/regression/1st_robust/save_model/pigmentation/state_dict.bin", "nc": 1},
    {"name": "reg_wrinkle_eye", "path": "checkpoint/regression/1st_robust/save_model/wrinkle_Ra/state_dict.bin", "nc": 1},
    # Classification Models
    {"name": "class_wrinkle", "path": "checkpoint/class/1st_robust/save_model/wrinkle/state_dict.bin", "nc": 7},
    {"name": "class_sagging", "path": "checkpoint/class/1st_robust/save_model/sagging/state_dict.bin", "nc": 6},
]

MODEL_TYPE = "coatnet_2_rw_224"

def convert_static_onnx(config):
    ckpt_path = config["path"]
    if not os.path.exists(ckpt_path):
        print(f"[X] File not found: {ckpt_path}")
        return

    print(f"[*] Static Export: {config['name']}...")
    
    # 1. Load PyTorch Model with Fixed Size & Exportable Flag
    # exportable=True replaces some dynamic ops with static ones in timm
    model = timm.create_model(MODEL_TYPE, pretrained=False, num_classes=config["nc"], img_size=224, exportable=True)
    checkpoint = torch.load(ckpt_path, map_location="cpu", weights_only=False)
    state_dict = checkpoint["model_state"] if "model_state" in checkpoint else checkpoint
    model.load_state_dict(state_dict)
    model.eval()

    # 2. ONNX Export (Static via Internal Tracing)
    dummy_input = torch.randn(1, 3, 224, 224)
    base_path = os.path.dirname(ckpt_path)
    onnx_path = os.path.join(base_path, "model_static.onnx")
    sim_path = os.path.join(base_path, "model_static_sim.onnx")

    try:
        # Use opset 17 for better support of ControlFlow if possible
        torch.onnx.export(
            model, dummy_input, onnx_path,
            export_params=True, opset_version=17,
            do_constant_folding=True,
            input_names=['input'], output_names=['output'],
            # No dynamic axes -> fully static
        )
    except Exception as e:
        print(f"    [X] Export failed: {e}")
        return

    # 3. Try Simplification immediately (via CLI)
    print(f"    [>] Running onnx-simplifier on static export...")
    try:
        cmd = ["onnxsim", onnx_path, sim_path]
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        
        if result.returncode == 0:
            print(f"    [V] Simplification Success! Saved to {sim_path}")
            
            # Compare sizes
            orig_size = os.path.getsize(onnx_path) / (1024*1024)
            sim_size = os.path.getsize(sim_path) / (1024*1024)
            print(f"        Size: {orig_size:.2f} MB -> {sim_size:.2f} MB")
            
        else:
            print(f"    [!] Simplification failed (CLI): {result.stderr}")
    except Exception as e:
        print(f"    [X] Simplification execution error: {e}")

    print("-" * 50)

if __name__ == "__main__":
    for config in MODEL_CONFIGS:
        convert_static_onnx(config)
