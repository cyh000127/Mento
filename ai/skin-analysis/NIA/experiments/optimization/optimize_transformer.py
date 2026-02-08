import os
import onnx
from onnxruntime.transformers import optimizer
from onnxruntime.transformers.fusion_options import FusionOptions

# Configuration
MODEL_CONFIGS = [
    # Regression Models (CoAtNet - Hybrid Transformer)
    {"name": "reg_moisture", "path": "checkpoint/regression/1st_robust/save_model/moisture/model.onnx"},
    {"name": "reg_pore", "path": "checkpoint/regression/1st_robust/save_model/pore/model.onnx"},
    {"name": "reg_pigmentation", "path": "checkpoint/regression/1st_robust/save_model/pigmentation/model.onnx"},
    {"name": "reg_wrinkle_eye", "path": "checkpoint/regression/1st_robust/save_model/wrinkle_Ra/model.onnx"},
    # Classification Models (MaxVit - Hybrid Transformer)
    {"name": "class_wrinkle", "path": "checkpoint/class/1st_robust/save_model/wrinkle/model.onnx"},
    {"name": "class_sagging", "path": "checkpoint/class/1st_robust/save_model/sagging/model.onnx"},
]

def optimize_transformer(config):
    input_model_path = config["path"]
    opt_model_path = input_model_path.replace(".onnx", "_opt.onnx")
    
    if not os.path.exists(input_model_path):
        print(f"[X] Input model not found: {input_model_path}")
        return

    print(f"[*] Transformer Optimization: {config['name']}...")
    
    try:
        # Fusion Options
        # BERT-like optimization might not perfectly fit CoAtNet/MaxVit, 
        # but basic fusions (Gelu, LayerNorm, Attention) often transfer.
        optimization_options = FusionOptions("bert")
        optimization_options.enable_gelu_approximation = True # Improve speed
        
        # Optimize
        # num_heads & hidden_size are guesses or auto-detected. 
        # For general optimization, we can leave them 0 to let ORT detect or skip specifics.
        optimized_model = optimizer.optimize_model(
            input_model_path,
            model_type='bert', # Closest approximation for general Transformer ops
            num_heads=0, 
            hidden_size=0,
            optimization_options=optimization_options
        )
        
        # Save optimized model
        optimized_model.save_model_to_file(opt_model_path)
        
        # Convert to FP16 (often safer with optimized models than raw conversion)
        try:
            optimized_model.convert_float_to_float16()
            fp16_path = opt_model_path.replace(".onnx", "_fp16.onnx")
            optimized_model.save_model_to_file(fp16_path)
            
            orig_size = os.path.getsize(input_model_path) / (1024 * 1024)
            opt_size = os.path.getsize(opt_model_path) / (1024 * 1024)
            fp16_size = os.path.getsize(fp16_path) / (1024 * 1024)
            
            print(f"    [V] Success: {opt_model_path}")
            print(f"        FP32 Size: {orig_size:.2f} MB -> {opt_size:.2f} MB")
            print(f"        FP16 Size: {fp16_size:.2f} MB ({orig_size/fp16_size:.1f}x reduction)")
            
        except Exception as e:
            print(f"    [!] FP16 Conversion failed: {e}")
            
    except Exception as e:
        print(f"    [X] Optimization failed: {e}")

    print("-" * 50)

if __name__ == "__main__":
    for config in MODEL_CONFIGS:
        optimize_transformer(config)
