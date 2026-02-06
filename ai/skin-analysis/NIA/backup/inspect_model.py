import torch
import os

checkpoint_path = "/home/j-i14a704/work/seunghun/S14P11A704/ai/skin-analysis/NIA/checkpoint/class/1st_cnn/save_model/pigmentation/state_dict.bin"

try:
    state_dict = torch.load(checkpoint_path, map_location="cpu", weights_only=False)
    model_keys = list(state_dict['model_state'].keys())
    print("Keys found in state_dict['model_state']:")
    for k in model_keys[:10]:
        print(k)
        
    print(f"\nTotal model keys: {len(model_keys)}")
    
    # Heuristic check
    if any("stem" in k for k in model_keys):
        print("Likely CoAtNet (found 'stem')")
    elif any("layer1" in k and "conv1" in k for k in model_keys):
        print("Likely ResNet (found 'layer1', 'conv1')")
    elif any("features" in k for k in model_keys):
        print("Likely VGG/EfficientNet-like (found 'features')")
    else:
        print("Architecture Unclear")
        
except Exception as e:
    print(f"Error loading checkpoint: {e}")
