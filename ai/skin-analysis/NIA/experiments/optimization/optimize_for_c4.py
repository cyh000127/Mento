import os
import onnx
import subprocess
from onnxruntime.quantization import quantize_dynamic, QuantType, shape_inference

# Configuration matching convert_to_onnx.py
MODEL_CONFIGS = [
    # Regression Models
    {"name": "reg_moisture", "path": "checkpoint/regression/1st_robust/save_model/moisture/model.onnx"},
    {"name": "reg_pore", "path": "checkpoint/regression/1st_robust/save_model/pore/model.onnx"},
    {"name": "reg_pigmentation", "path": "checkpoint/regression/1st_robust/save_model/pigmentation/model.onnx"},
    {"name": "reg_wrinkle_eye", "path": "checkpoint/regression/1st_robust/save_model/wrinkle_Ra/model.onnx"},
    # Classification Models
    {"name": "class_wrinkle", "path": "checkpoint/class/1st_robust/save_model/wrinkle/model.onnx"},
    {"name": "class_sagging", "path": "checkpoint/class/1st_robust/save_model/sagging/model.onnx"},
]

def optimize_for_c4(config):
    input_model_path = config["path"]
    sim_model_path = input_model_path.replace(".onnx", "_sim.onnx")
    quant_model_path = input_model_path.replace(".onnx", "_c4_quant.onnx")
    
    if not os.path.exists(input_model_path):
        print(f"[X] Input model not found: {input_model_path}")
        return

    print(f"[*] Optimizing {config['name']} for C4 (Haswell/Broadwell)...")
    
    # 1. Simplify Graph (onnx-simplifier CLI)
    print(f"    [1/2] Running onnx-simplifier (CLI)...")
    try:
        # Try running onnxsim as a subprocess
        cmd = ["onnxsim", input_model_path, sim_model_path]
        result = subprocess.run(cmd, stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        
        if result.returncode == 0:
            print(f"    [V] Simplification success.")
        else:
            print(f"    [!] Simplification failed (CLI): {result.stderr}")
            # Fallback to python import if available (or skip)
            sim_model_path = input_model_path
            
    except Exception as e:
        print(f"    [X] Simplification execution error: {e}")
        sim_model_path = input_model_path
        
    # 2. INT8 Quantization (Dynamic)
    print(f"    [2/2] Attempting INT8 Quantization...")
    try:
        # Pre-process for shape inference
        pre_path = input_model_path.replace(".onnx", "_pre.onnx")
        shape_inference.quant_pre_process(
            input_model_path=sim_model_path,
            output_model_path=pre_path,
            skip_symbolic_shape=False,
            auto_merge=True
        )
        quantize_dynamic(
            model_input=pre_path,
            model_output=quant_model_path,
            weight_type=QuantType.QUInt8,
        )
        
        orig_size = os.path.getsize(input_model_path) / (1024 * 1024)
        quant_size = os.path.getsize(quant_model_path) / (1024 * 1024)
        
        print(f"    [V] C4 Quantization Success: {orig_size:.2f} MB -> {quant_size:.2f} MB")
        
        # Cleanup temp
        if os.path.exists(pre_path): os.remove(pre_path)
        
    except Exception as e:
        print(f"    [X] Quantization failed: {e}")
        if os.path.exists(quant_model_path): os.remove(quant_model_path)

    print("-" * 50)

if __name__ == "__main__":
    for config in MODEL_CONFIGS:
        optimize_for_c4(config)
