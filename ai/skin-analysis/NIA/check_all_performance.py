import torch, timm, onnxruntime as ort
import numpy as np
import time, os

def benchmark():
    # moisture 모델 하나를 대표로 테스트
    folder = "checkpoint/regression/1st_coat/save_model/moisture"
    pt_path = os.path.join(folder, "state_dict.bin")
    onnx_fp32 = os.path.join(folder, "model.onnx")
    onnx_int8 = os.path.join(folder, "model_quant.onnx")
    
    dummy_np = np.random.randn(1, 3, 224, 224).astype(np.float32)
    
    # 1. PyTorch
    print("[*] PyTorch 테스트 중...")
    pt_model = timm.create_model("coatnet_2_rw_224", pretrained=False, num_classes=1).eval()
    pt_model.load_state_dict(torch.load(pt_path, map_location="cpu", weights_only=False))
    
    start = time.time()
    for _ in range(10): out_pt = pt_model(torch.from_numpy(dummy_np)).detach().numpy()
    t_pt = (time.time() - start) / 10

    # 2. ONNX FP32
    print("[*] ONNX FP32 테스트 중...")
    sess_fp32 = ort.InferenceSession(onnx_fp32, providers=['CPUExecutionProvider'])
    in_name = sess_fp32.get_inputs()[0].name
    start = time.time()
    for _ in range(10): out_fp32 = sess_fp32.run(None, {in_name: dummy_np})[0]
    t_fp32 = (time.time() - start) / 10

    # 3. ONNX INT8
    print("[*] ONNX INT8 테스트 중...")
    if not os.path.exists(onnx_int8):
        print(f"[X] INT8 모델이 없습니다: {onnx_int8}")
        t_int8, out_int8 = 0, np.array([0])
    else:
        sess_int8 = ort.InferenceSession(onnx_int8, providers=['CPUExecutionProvider'])
        start = time.time()
        for _ in range(10): out_int8 = sess_int8.run(None, {in_name: dummy_np})[0]
        t_int8 = (time.time() - start) / 10

    print(f"
{'Model':<15} | {'Latency':<10} | {'Error (vs PT)':<15}")
    print("-" * 50)
    print(f"{'PyTorch':<15} | {t_pt*1000:>7.2f}ms | {'0.000000':<15}")
    print(f"{'ONNX FP32':<15} | {t_fp32*1000:>7.2f}ms | {np.max(np.abs(out_pt - out_fp32)):<15.6f}")
    if os.path.exists(onnx_int8):
        print(f"{'ONNX INT8':<15} | {t_int8*1000:>7.2f}ms | {np.max(np.abs(out_pt - out_int8)):<15.6f}")

if __name__ == "__main__":
    benchmark()
