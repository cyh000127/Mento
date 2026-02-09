import os
import onnx
from onnxruntime.quantization import quantize_dynamic, QuantType, shape_inference
from onnxconverter_common import float16

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

def optimize_model(config):
    input_model_path = config["path"]
    int8_model_path = input_model_path.replace(".onnx", "_quant.onnx")
    fp16_model_path = input_model_path.replace(".onnx", "_fp16.onnx")
    
    if not os.path.exists(input_model_path):
        print(f"[X] Input model not found: {input_model_path}")
        return

    print(f"[*] Optimization Target: {config['name']}")
    orig_size = os.path.getsize(input_model_path) / (1024 * 1024)
    print(f"    Original Size: {orig_size:.2f} MB")

    # 1. Attempt INT8 Quantization
    print(f"    [>] Attempting INT8 Quantization...")
    try:
        quantize_dynamic(
            model_input=input_model_path,
            model_output=int8_model_path,
            weight_type=QuantType.QUInt8,
        )
        quant_size = os.path.getsize(int8_model_path) / (1024 * 1024)
        print(f"    [V] INT8 Success: {quant_size:.2f} MB ({orig_size/quant_size:.1f}x)")
    except Exception as e:
        print(f"    [X] INT8 Failed: {e}")
        if os.path.exists(int8_model_path): os.remove(int8_model_path)

    # 2. Attempt FP16 Conversion
    print(f"    [>] Attempting FP16 Conversion...")
    try:
        model = onnx.load(input_model_path)
        fp16_model = float16.convert_float_to_float16(model)
        onnx.save(fp16_model, fp16_model_path)
        
        fp16_size = os.path.getsize(fp16_model_path) / (1024 * 1024)
        print(f"    [V] FP16 Success: {fp16_size:.2f} MB ({orig_size/fp16_size:.1f}x)")
    except Exception as e:
        print(f"    [X] FP16 Failed: {e}")
        if os.path.exists(fp16_model_path): os.remove(fp16_model_path)
        
    print("-" * 50)

if __name__ == "__main__":
    for config in MODEL_CONFIGS:
        optimize_model(config)
