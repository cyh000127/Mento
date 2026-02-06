# Project Structure & Agent Guide

## 1. Overview
This project is an AI-powered Skin Analysis system (Project NIA) designed to analyze facial skin conditions using multi-angle images (Front, Left 30, Right 30).
It utilizes deep learning models (CoatNet) for both **Classification** (features like Wrinkles, Sagging) and **Regression** (features like Pigmentation, Moisture, Pores).

## 2. Directory Structure

```
NIA/
├── checkpoint/                 # Trained model weights
│   ├── class/                  # Classification models
│   │   ├── 1st_cnn/            # Legacy model (Poor performance)
│   │   └── 1st_coat/           # Production model (Recommended)
│   └── regression/             # Regression models
│       └── 1st_coat/           # Production model (Recommended)
│
├── dataset/                    # Training and inference data
│   ├── img/                    # Raw images (organized by device/subject)
│   ├── label/                  # JSON labels (ground truth)
│   └── split/                  # Train/Test split definitions
│
├── tool/                       # logical Source code
│   ├── analysis/               # Inference Pipeline & Analysis logic
│   │   ├── inference.py        # Main entry point for Multi-View Inference
│   │   ├── calibration.py      # Face landmark calibration logic
│   │   └── test_multiview.py   # Verification script for inference
│   │
│   ├── data_loader.py          # Dataset loading & augmentation
│   ├── model.py                # Model architecture definitions
│   ├── main.py                 # Training entry point
│   └── test.py                 # Evaluation entry point
│
├── wandb/                      # Weight & Biases logs
└── requirements.txt            # Python dependencies
```

## 3. Key Workflows

### A. Inference (Production)
To run skin analysis on a new subject with 3-view images:
```bash
python tool/analysis/inference.py --front <FRONT_IMG> --l30 <L30_IMG> --r30 <R30_IMG>
```
*   **Logic**:
    1.  Detects face landmarks using Mediapipe.
    2.  Crops specific ROIs (Region of Interest) based on `calibration_rules.json`.
    3.  Routes each ROI to the appropriate model (e.g., Left Cheek -> Pigmentation Regression Model).
    4.  Aggregates results into a JSON object.

### B. Training (Development)
To train a model (e.g., classification):
```bash
python tool/main.py --mode class --name <EXP_NAME> --equ <EQUIPMENT_ID> ...
```

### C. Evaluation (Development)
To evaluate model performance on test set:
```bash
python tool/test.py --mode <class|regression> --name <EXP_NAME>
```

## 4. Model & Data Conventions

### Face Parts
The system maps facial regions to specific IDs:
*   `1`: Forehead
*   `2`: Glabellus (Between eyebrows)
*   `3`: Right Perocular (User View: Right eye area) - Code Key: `l_perocular` (Left side of image)
*   `4`: Left Perocular (User View: Left eye area) - Code Key: `r_perocular` (Right side of image)
*   `5`: Right Cheek - Code Key: `l_cheek`
*   `6`: Left Cheek - Code Key: `r_cheek`
*   `7`: Lip
*   `8`: Chin

### Analysis Features
*   **Regression**: Pigmentation, Pore, Moisture, Elasticity. (Use for these features as they show higher correlation).
*   **Classification**: Wrinkle, Sagging. (Use for these features as regression data is sparse/noisy).

## 5. Agent Tips
*   **Env**: Use `uv run` or activate `.venv` when running python commands.
*   **Paths**: Always use absolute paths or paths relative to project root `NIA/`.
*   **Visuals**: Use `inference.py` output images (`*_result.jpg`) to debug cropping issues.
