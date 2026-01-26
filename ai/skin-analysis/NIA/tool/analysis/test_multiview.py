import os
import sys
import json
import logging

# Add workspace to path
sys.path.append(os.path.dirname(os.path.dirname(os.path.dirname(os.path.abspath(__file__)))))

from tool.analysis.inference import SkinAnalyzer

def test_pipeline():
    # Paths to sample images
    # Adjust these if the actual path structure is different
    base_dir = "dataset/img/01/0162"
    front = os.path.join(base_dir, "0162_01_F.jpg")
    l30 = os.path.join(base_dir, "0162_01_L30.jpg")
    r30 = os.path.join(base_dir, "0162_01_R30.jpg")
    
    print(f"Testing pipeline with:\n Front: {front}\n L30: {l30}\n R30: {r30}")
    
    if not all(os.path.exists(p) for p in [front, l30, r30]):
        print("Error: One or more sample images not found.")
        return

    # Initialize Analyzer
    try:
        analyzer = SkinAnalyzer()
    except Exception as e:
        print(f"Failed to initialize SkinAnalyzer: {e}")
        return

    # Run Prediction
    try:
        results = analyzer.predict_multiview(front, l30, r30)
        print("\n=== Inference Results ===")
        print(json.dumps(results, indent=2))
        
        # Simple Validation
        assert "front" in results
        assert "l30" in results
        assert "r30" in results
        
        print("\n[SUCCESS] Pipeline validation passed.")
        
    except Exception as e:
        print(f"\n[FAILURE] Pipeline execution failed: {e}")
        import traceback
        traceback.print_exc()

if __name__ == "__main__":
    test_pipeline()
