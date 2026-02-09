import os
from onnxruntime.quantization import quantize_dynamic, QuantType

MODEL_CONFIGS = [
    "checkpoint/regression/1st_coat/save_model/moisture",
    "checkpoint/regression/1st_coat/save_model/pore",
    "checkpoint/regression/1st_coat/save_model/pigmentation",
    "checkpoint/regression/1st_coat/save_model/wrinkle_Ra",
    "checkpoint/class/1st_coat/save_model/wrinkle",
    "checkpoint/class/1st_coat/save_model/sagging",
]

def run_quantization():
    for folder in MODEL_CONFIGS:
        # 절대 경로로 변환하여 파일 경로 문제 방지
        abs_folder = os.path.abspath(folder)
        input_model = os.path.join(abs_folder, "model.onnx")
        output_model = os.path.join(abs_folder, "model_quant.onnx")
        
        if not os.path.exists(input_model):
            print(f"[X] 원본 ONNX 없음: {input_model}")
            continue
            
        print(f"[*] 양자화 중: {input_model}")
        
        try:
            # Shape Inference를 비활성화하여 에러 우회
            quantize_dynamic(
                model_input=input_model,
                model_output=output_model,
                weight_type=QuantType.QUInt8,
                extra_options={'DisableShapeInference': True}
            )
            
            orig_size = os.path.getsize(input_model) / (1024*1024)
            if os.path.exists(input_model + ".data"):
                orig_size += os.path.getsize(input_model + ".data") / (1024*1024)
                
            quant_size = os.path.getsize(output_model) / (1024*1024)
            
            print(f"[V] 완료 -> 용량: {orig_size:.1f}MB -> {quant_size:.1f}MB")
            
        except Exception as e:
            # 에러 발생 시 전체 트레이스백 출력
            import traceback
            traceback.print_exc()
            print(f"[!] 변환 실패: {e}")
        
        print("-" * 30)

if __name__ == "__main__":
    run_quantization()
