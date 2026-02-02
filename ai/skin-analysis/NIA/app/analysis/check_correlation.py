import os
import json
import numpy as np
import glob
from collections import defaultdict

def check_data_logic():
    label_root = "dataset/label/01" # Only checking digital_camera for now
    
    # 1. Wrinkle Correlation: Grade (Class) vs Ra (Reg)
    # We look at "perocular" area files (Area code 03, 04)
    wrinkle_grades = []
    wrinkle_ra = []
    
    # 2. Pigmentation Logic: Where does the count come from?
    # We look at "cheek" area files (Area code 05, 06) and "front" (00)
    cheek_pigmentation_keys = set()
    front_pigmentation_keys = set()
    
    print("Scanning label files...")
    
    # Iterate through a subset of subjects to save time
    subjects = os.listdir(label_root)
    # subjects = subjects[:50] 
    
    for sub in subjects:
        sub_dir = os.path.join(label_root, sub)
        if not os.path.isdir(sub_dir): continue
        
        json_files = glob.glob(os.path.join(sub_dir, "*.json"))
        
        for jf in json_files:
            with open(jf, "r") as f:
                data = json.load(f)
            
            fname = os.path.basename(jf)
            # Area code is last part before .json: 0001_01_F_03.json -> 03
            area_code = fname.split("_")[-1].split(".")[0]
            
            # --- Wrinkle Check (Area 03, 04) ---
            if area_code in ["03", "04"]: # L/R Perocular
                # Check Annotations (Grade)
                for k, v in data.get("annotations", {}).items():
                    if "wrinkle" in k:
                        # Find corresponding Ra in equipment
                        ra_key = k + "_Ra"
                        if ra_key in data.get("equipment", {}):
                            ra_val = data["equipment"][ra_key]
                            if v is not None and ra_val is not None:
                                wrinkle_grades.append(v)
                                wrinkle_ra.append(ra_val)

            # --- Pigmentation Check ---
            # Check what keys exist in Cheek files (05, 06) vs Front (00)
            if area_code in ["05", "06"]: # Cheeks
                if "equipment" in data:
                    for k in data["equipment"].keys():
                        if "pigmentation" in k or "spot" in k:
                            cheek_pigmentation_keys.add(k)
                            
            if area_code == "00": # Front
                if "equipment" in data:
                    for k in data["equipment"].keys():
                        if "pigmentation" in k or "spot" in k:
                            front_pigmentation_keys.add(k)

    print("\n=== Wrinkle Analysis ===")
    print(f"Sample Count: {len(wrinkle_grades)}")
    if len(wrinkle_grades) > 0:
        cor = np.corrcoef(wrinkle_grades, wrinkle_ra)[0, 1]
        print(f"Correlation (Grade vs Ra): {cor:.4f}")
        
        # Calculate Ra ranges per Grade
        grade_ra_map = defaultdict(list)
        for g, r in zip(wrinkle_grades, wrinkle_ra):
            grade_ra_map[g].append(r)
            
        print("\nRa Stats per Grade:")
        for g in sorted(grade_ra_map.keys()):
            vals = grade_ra_map[g]
            print(f"Grade {g}: Mean Ra={np.mean(vals):.2f}, Min={np.min(vals):.2f}, Max={np.max(vals):.2f} (n={len(vals)})")

    print("\n=== Pigmentation Key Check ===")
    print(f"Keys found in Cheek Files (05/06): {cheek_pigmentation_keys}")
    print(f"Keys found in Front Files (00): {front_pigmentation_keys}")

    # If Cheek files have no pigmentation count, verify data_loader assumption
    if not cheek_pigmentation_keys:
        print("\n[WARNING] No 'pigmentation' keys found in Cheek equipment data.")
        print("This suggests the Regression Model might be trained on 'Front' images OR uses a key we missed.")

if __name__ == "__main__":
    check_data_logic()
