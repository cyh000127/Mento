import torch
import timm
import os

MODEL_CONFIGS = [
    {"name": "reg_moisture", "path": "checkpoint/regression/1st_robust/save_model/moisture/state_dict.bin", "nc": 1},
    {"name": "reg_pore", "path": "checkpoint/regression/1st_robust/save_model/pore/state_dict.bin", "nc": 1},
    {"name": "reg_pigmentation", "path": "checkpoint/regression/1st_robust/save_model/pigmentation/state_dict.bin", "nc": 1},
    {"name": "reg_wrinkle_eye", "path": "checkpoint/regression/1st_robust/save_model/wrinkle_Ra/state_dict.bin", "nc": 1},
    {"name": "class_wrinkle", "path": "checkpoint/class/1st_robust/save_model/wrinkle/state_dict.bin", "nc": 7},
    {"name": "class_sagging", "path": "checkpoint/class/1st_robust/save_model/sagging/state_dict.bin", "nc": 6},
]

MODEL_TYPE = "coatnet_2_rw_224"

def convert_to_onnx(config):
    ckpt_path = config["path"]
    if not os.path.exists(ckpt_path):
        print(f"[X] File not found: {ckpt_path}")
        return

    print(f"[*] Converting: {config['name']}")
    
    model = timm.create_model(MODEL_TYPE, pretrained=False, num_classes=config["nc"])
    checkpoint = torch.load(ckpt_path, map_location="cpu", weights_only=False)
    state_dict = checkpoint.get("model_state", checkpoint)
    model.load_state_dict(state_dict)
    model.eval()

    dummy_input = torch.randn(1, 3, 224, 224)
    onnx_path = os.path.join(os.path.dirname(ckpt_path), "model.onnx")

    torch.onnx.export(
        model, dummy_input, onnx_path,
        export_params=True, opset_version=14,
        do_constant_folding=True,
        input_names=['input'], output_names=['output']
    )

    print(f"[V] Done: {onnx_path}")

if __name__ == "__main__":
    for config in MODEL_CONFIGS:
        convert_to_onnx(config)

