import pandas as pd
import json
import os
import numpy as np

# Paths
BASE_DIR = os.path.dirname(os.path.abspath(__file__))
MEASUREMENT_PATH = os.path.join(BASE_DIR, "../../dataset/measurement_data.csv")
META_PATH = os.path.join(BASE_DIR, "../../dataset/meta_data.csv")
OUTPUT_PATH = os.path.join(BASE_DIR, "../resources/reference_stats.json")

def calculate_percentiles(series):
    if series.empty: return {}
    # Calculate 0 to 100 percentiles
    return {str(i): float(np.percentile(series.dropna(), i)) for i in range(101)}

def get_age_group(age):
    if pd.isna(age): return "unknown"
    try:
        age_int = int(age)
    except:
        return "unknown"
        
    if age_int < 20: return "10s"
    elif age_int < 30: return "20s"
    elif age_int < 40: return "30s"
    elif age_int < 50: return "40s"
    elif age_int < 60: return "50s"
    else: return "60s_plus"

def get_gender_key(gender_str):
    if pd.isna(gender_str): return "unknown"
    g = str(gender_str).strip()
    if g == "남성": return "male"
    if g == "여성": return "female"
    return "unknown"

def main():
    if not os.path.exists(MEASUREMENT_PATH) or not os.path.exists(META_PATH):
        print(f"Error: Data files not found.")
        return

    # Load Data
    meas_df = pd.read_csv(MEASUREMENT_PATH)
    meta_df = pd.read_csv(META_PATH)
    
    # Merge on subject_no
    # Check headers
    # meas: subject_no, ...
    # meta: subject_no, 성별, 나이, ...
    
    merged_df = pd.merge(meas_df, meta_df, on="subject_no", how="inner")
    
    # Define mapping keys based on merged data
    merged_df["age_group"] = merged_df["나이"].apply(get_age_group)
    merged_df["gender_key"] = merged_df["성별"].apply(get_gender_key)
    
    stats_db = {}
    
    # Helper to generate stats for a specific filtered dataframe
    def process_df(df, key_prefix):
        # 1. Pigmentation
        if "스팟개수_정면" in df.columns:
            if key_prefix not in stats_db: stats_db[key_prefix] = {}
            stats_db[key_prefix]["pigmentation"] = calculate_percentiles(df["스팟개수_정면"])

        # 2. Pore (Aggregated cheeks)
        pore_cols = ["모공개수_오른쪽볼", "모공개수_왼쪽볼"]
        pore_data = pd.concat([df[col] for col in pore_cols if col in df.columns])
        if not pore_data.empty:
            if key_prefix not in stats_db: stats_db[key_prefix] = {}
            stats_db[key_prefix]["pore"] = calculate_percentiles(pore_data)

        # 3. Moisture (Aggregated cheeks)
        moisture_cols = ["수분_오른쪽볼", "수분_왼쪽볼"]
        moisture_data = pd.concat([df[col] for col in moisture_cols if col in df.columns])
        if not moisture_data.empty:
            if key_prefix not in stats_db: stats_db[key_prefix] = {}
            stats_db[key_prefix]["moisture"] = calculate_percentiles(moisture_data)

        # 4. Wrinkle (Ra - Aggregated perocular)
        wrinkle_cols = ["주름_왼쪽눈가_Ra", "주름_오른쪽눈가_Ra"]
        wrinkle_data = pd.concat([df[col] for col in wrinkle_cols if col in df.columns])
        if not wrinkle_data.empty:
            if key_prefix not in stats_db: stats_db[key_prefix] = {}
            stats_db[key_prefix]["wrinkle_ra"] = calculate_percentiles(wrinkle_data)
            
        # 5. Elasticity (R0 or R2? Usually R2 is gross elasticity. Let's use R2)
        # Columns: 탄력_왼쪽볼_R2, 탄력_오른쪽볼_R2
        elas_cols = ["탄력_왼쪽볼_R2", "탄력_오른쪽볼_R2"]
        elas_data = pd.concat([df[col] for col in elas_cols if col in df.columns])
        if not elas_data.empty:
            if key_prefix not in stats_db: stats_db[key_prefix] = {}
            stats_db[key_prefix]["elasticity"] = calculate_percentiles(elas_data)

    # 1. Global Stats
    process_df(merged_df, "global")
    print("Processed Global Stats")
    
    # 2. Segmented Stats
    groups = merged_df.groupby(["gender_key", "age_group"])
    for (gender, age_grp), group_df in groups:
        if gender == "unknown" or age_grp == "unknown": continue
        
        key = f"{gender}_{age_grp}"
        process_df(group_df, key)
        print(f"Processed {key} (N={len(group_df)})")

    # Save
    with open(OUTPUT_PATH, "w") as f:
        json.dump(stats_db, f, indent=2)
    print(f"Saved extended stats to {OUTPUT_PATH}")

if __name__ == "__main__":
    main()
