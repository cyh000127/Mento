# Skin Analysis Pipeline Diagnosis & Fix Report

## 1. Problem Diagnosis
After a deep dive into the codebase and data pipeline, I identified critical issues causing poor performance:

### A. Training vs. Inference Domain Gap (Critical)
- **Issue**: The model was trained on manually labeled bounding boxes (`dataset/label/...`), but inference uses MediaPipe-based cropping logic.
- **Finding**: My analysis script (`debug_crop_alignment.py`) revealed a systematic misalignment:
    - **Shift**: Inference crops were shifted ~100px to the left and ~40px down compared to training crops (on 224x224 scale).
    - **Scale**: Inference crops were ~15% larger (zoomed out) than training crops.
- **Impact**: The model was looking at the wrong skin region during inference.

### B. Broken Training Logic (Fixed & Upgraded)
- **Issue**:
    1.  **Early Stopping**: Was never resetting the counter.
    2.  **Stagnation**: Model trained for 25+ epochs with no improvement.
    3.  **Inefficiency**: Training loop was running for ALL features, including unused legacy ones (e.g., `elasticity_R2`, `dryness`).
- **Impact**: Models were underfitting and training took twice as long as needed.

### C. Python 3.12 Compatibility (Addressed)
- **Issue**: The original codebase (Python 3.9) had potential conflicts with Python 3.12 (e.g., `np.Inf`).
- **Fix**: Replaced `np.Inf` with `np.inf`. Corrected `pyproject.toml`.

---

## 2. Implemented Fixes

### A. Training Loop Optimization (NEW)
I have streamlined the training process in `core/main.py`:
1.  **Focused Training**: Modified the loop to ONLY train models used in `service_inference.py`.
    -   **Regression**: `moisture`, `pore`, `pigmentation`, `wrinkle_Ra`. (Removed `elasticity_R2`).
    -   **Classification**: `sagging`, `wrinkle`. (Removed `dryness`, `pore-class`, `pigmentation-class`).
2.  **Modern Best Practices**: Added `ReduceLROnPlateau`, `AdamW`, `EarlyStopping`, and `Gradient Clipping` to `core/model.py`.

### B. Calibration Rules Optimization (Immediate Fix)
- **Action**: Updated `app/resources/calibration_rules.json` with optimized parameters to alignment inference crops with training data.

### C. Dataset Regeneration (Robust Fix - In Progress)
- **Action**: Created `preprocess_dataset_mediapipe.py` which is currently generating a new dataset in `dataset/cropped_aligned`.
- **Action**: Updated `core/data_loader.py` to point to this new dataset directory.

---

## 3. Recommended Next Steps

1.  **Wait for Preprocessing**: The script `preprocess_dataset_mediapipe.py` is running in the background.
2.  **Retrain Models**: Once the dataset is ready, run the training. The new `core/model.py` will handle everything efficiently.
    ```bash
    # Retrain Regression Models (Moisture, Pore, Pigmentation, Wrinkle_Ra)
    uv run python core/main.py --mode regression --name 3rd_robust_training --equ 1 2 3 --epoch 100

    # Retrain Classification Models (Sagging, Wrinkle)
    uv run python core/main.py --mode class --name 3rd_robust_training --equ 1 2 3 --epoch 100
    ```
3.  **Verify**: Test with `app/analysis/service_inference.py`. The robust training + aligned data should drastically improve performance.
