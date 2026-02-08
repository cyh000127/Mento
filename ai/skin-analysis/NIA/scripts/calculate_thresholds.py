import os
import numpy as np

BASE_PATH = "checkpoint/regression/1st_robust/prediction"
PRED_FILE = os.path.join(BASE_PATH, "pred.txt")

CATEGORIES = {"moisture": [], "pore": [], "pigmentation": [], "wrinkle_Ra": []}

def calculate_thresholds():
    if not os.path.exists(PRED_FILE):
        print(f"Error: {PRED_FILE} not found.")
        return

    with open(PRED_FILE, "r") as f:
        for line in f:
            parts = line.strip().split(",")
            if len(parts) < 3:
                continue
            area = parts[1].strip()
            try:
                pred_val = float(parts[2].strip())
            except ValueError:
                continue
            for key in CATEGORIES:
                if key in area:
                    CATEGORIES[key].append(pred_val)
                    break

    print("Thresholds (20%, 40%, 60%, 80%):")
    for key, values in CATEGORIES.items():
        if not values:
            print(f"{key}: No data")
            continue
        arr = np.array(values)
        thresholds = [round(np.percentile(arr, p), 1) for p in [20, 40, 60, 80]]
        print(f'"{key}": {thresholds},')

if __name__ == "__main__":
    calculate_thresholds()
