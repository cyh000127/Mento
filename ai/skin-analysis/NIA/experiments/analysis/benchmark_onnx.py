import torch
import timm
import onnxruntime as ort
import numpy as np
import time
import os
import psutil

MODEL_CONFIGS = [
    {"name": "reg_moisture", "pt_path": "checkpoint/regression/1st_robust/save_model/moisture/state_dict.bin", 
     "onnx_path": "checkpoint/regression/1st_robust/save_model/moisture/model.onnx", "nc": 1},
    {"name": "reg_pore", "pt_path": "checkpoint/regression/1st_robust/save_model/pore/state_dict.bin", 
     "onnx_path": "checkpoint/regression/1st_robust/save_model/pore/model.onnx", "nc": 1},
    {"name": "reg_pigmentation", "pt_path": "checkpoint/regression/1st_robust/save_model/pigmentation/state_dict.bin", 
     "onnx_path": "checkpoint/regression/1st_robust/save_model/pigmentation/model.onnx", "nc": 1},
    {"name": "reg_wrinkle_eye", "pt_path": "checkpoint/regression/1st_robust/save_model/wrinkle_Ra/state_dict.bin", 
     "onnx_path": "checkpoint/regression/1st_robust/save_model/wrinkle_Ra/model.onnx", "nc": 1},
    {"name": "class_wrinkle", "pt_path": "checkpoint/class/1st_robust/save_model/wrinkle/state_dict.bin", 
     "onnx_path": "checkpoint/class/1st_robust/save_model/wrinkle/model.onnx", "nc": 7},
    {"name": "class_sagging", "pt_path": "checkpoint/class/1st_robust/save_model/sagging/state_dict.bin", 
     "onnx_path": "checkpoint/class/1st_robust/save_model/sagging/model.onnx", "nc": 6},
]

MODEL_TYPE = "coatnet_2_rw_224"

def get_memory_mb():
    return psutil.Process(os.getpid()).memory_info().rss / 1024 / 1024

def benchmark_pytorch(model, dummy_pt, warmup=5, runs=20):
    for _ in range(warmup):
        model(dummy_pt)
    start = time.time()
    for _ in range(runs):
        with torch.no_grad():
            out = model(dummy_pt).detach().numpy()
    return (time.time() - start) / runs * 1000, out

def benchmark_onnx(sess, dummy_np, warmup=5, runs=20):
    in_name = sess.get_inputs()[0].name
    for _ in range(warmup):
        sess.run(None, {in_name: dummy_np})
    start = time.time()
    for _ in range(runs):
        out = sess.run(None, {in_name: dummy_np})[0]
    return (time.time() - start) / runs * 1000, out

def main():
    dummy_np = np.random.randn(1, 3, 224, 224).astype(np.float32)
    dummy_pt = torch.from_numpy(dummy_np)

    print(f"{'Model':<20} | {'Type':<12} | {'Latency (ms)':>12} | {'Memory (MB)':>12} | {'Error':>12}")
    print("-" * 80)

    for cfg in MODEL_CONFIGS:
        if not os.path.exists(cfg["pt_path"]):
            print(f"[X] Missing: {cfg['pt_path']}")
            continue

        mem_start = get_memory_mb()
        model = timm.create_model(MODEL_TYPE, pretrained=False, num_classes=cfg["nc"]).eval()
        ckpt = torch.load(cfg["pt_path"], map_location="cpu", weights_only=False)
        model.load_state_dict(ckpt.get("model_state", ckpt))
        
        lat_pt, out_pt = benchmark_pytorch(model, dummy_pt)
        mem_pt = get_memory_mb() - mem_start
        print(f"{cfg['name']:<20} | {'PyTorch':<12} | {lat_pt:>12.2f} | {mem_pt:>12.2f} | {'-':>12}")
        del model

        if os.path.exists(cfg["onnx_path"]):
            mem_start = get_memory_mb()
            sess = ort.InferenceSession(cfg["onnx_path"], providers=['CPUExecutionProvider'])
            lat_onnx, out_onnx = benchmark_onnx(sess, dummy_np)
            mem_onnx = get_memory_mb() - mem_start
            error = np.max(np.abs(out_pt - out_onnx))
            print(f"{cfg['name']:<20} | {'ONNX':<12} | {lat_onnx:>12.2f} | {mem_onnx:>12.2f} | {error:>12.6f}")
            del sess

        print("-" * 80)

if __name__ == "__main__":
    main()
