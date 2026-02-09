import torch
import torch.nn as nn
import torch.nn.utils.prune as prune
import timm
import os
import onnx
import numpy as np

# Configuration matching convert_to_onnx.py
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
PRUNING_AMOUNT = 0.2  # 20% of weights pruned

def prune_and_export_onnx(config):
    ckpt_path = config["path"]
    if not os.path.exists(ckpt_path):
        print(f"[X] File not found: {ckpt_path}")
        return

    print(f"[*] Pruning & Export: {config['name']} (Amount: {PRUNING_AMOUNT*100}%)")
    
    # 1. Load PyTorch Model
    model = timm.create_model(MODEL_TYPE, pretrained=False, num_classes=config["nc"])
    checkpoint = torch.load(ckpt_path, map_location="cpu", weights_only=False)
    state_dict = checkpoint["model_state"] if "model_state" in checkpoint else checkpoint
    model.load_state_dict(state_dict)
    model.eval()

    # 2. Apply Pruning (L1 Unstructured)
    # Prune 20% of connections in all Conv2d and Linear layers
    parameters_to_prune = []
    for module in model.modules():
        if isinstance(module, torch.nn.Conv2d) or isinstance(module, torch.nn.Linear):
            parameters_to_prune.append((module, 'weight'))

    prune.global_unstructured(
        parameters_to_prune,
        pruning_method=prune.L1Unstructured,
        amount=PRUNING_AMOUNT,
    )
    
    # Make pruning permanent (remove masks, burn into weights)
    for module, _ in parameters_to_prune:
        prune.remove(module, 'weight')

    print(f"    [V] Pruning applied and made permanent.")

    # 3. ONNX Export
    dummy_input = torch.randn(1, 3, 224, 224)
    base_path = os.path.dirname(ckpt_path)
    onnx_path = os.path.join(base_path, "model_pruned.onnx")

    torch.onnx.export(
        model, dummy_input, onnx_path,
        export_params=True, opset_version=17, # Use 17 to avoid down-conversion error
        do_constant_folding=True,
        input_names=['input'], output_names=['output']
    )
    
    orig_size = os.path.getsize(ckpt_path) / (1024*1024)
    onnx_size = os.path.getsize(onnx_path) / (1024*1024)
    print(f"    [V] Export Success: {onnx_path}")
    print(f"        Ckpt Size: {orig_size:.2f} MB -> Pruned ONNX Size: {onnx_size:.2f} MB")
    
    # 4. NOTE: ONNX Simplifier
    # We deliberately SKIP onnx-simplifier on the full graph because we know it fails structure-wise.
    # However, the weights inside 'model_pruned.onnx' are now sparse (zeros).

    print("-" * 50)

if __name__ == "__main__":
    for config in MODEL_CONFIGS:
        prune_and_export_onnx(config)
