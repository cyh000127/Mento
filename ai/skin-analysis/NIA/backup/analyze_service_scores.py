import pandas as pd
import numpy as np
import warnings

warnings.filterwarnings('ignore')

def load_predictions(file_path):
    data = []
    with open(file_path, 'r') as f:
        for line in f:
            parts = line.strip().split(',')
            if len(parts) >= 3:
                angle = parts[0].strip()
                attr = parts[1].strip()
                try:
                    val = float(parts[2].strip())
                    # Extract Subject ID (Sub_XXXX)
                    filename = parts[3].strip()
                    sub_id = "_".join(filename.split('_')[:2]) # e.g., Sub_0304
                    data.append({'sub_id': sub_id, 'angle': angle, 'attribute': attr, 'value': val})
                except ValueError:
                    continue
    return pd.DataFrame(data)

def analyze_and_propose():
    print("Loading predictions...")
    reg_df = load_predictions('checkpoint/regression/1st_coat/prediction/pred.txt')
    cls_df = load_predictions('checkpoint/class/1st_coat/prediction/pred.txt')
    
    # 1. Moisture (Regression, L30/R30 Avg)
    print("\n--- MOISTURE (L30/R30 Avg) ---")
    moist_df = reg_df[reg_df['attribute'].isin(['cheek_moisture'])] # Assuming cheek_moisture covers the main area
    # Pivot to get L30 and R30 columns per subject
    # Note in logs: angles are L30, R30, F, L, R
    
    # Check if 'L30' exists in angles, if not try L and R if L30 missing (or check exact strings)
    # Based on summary, angles are L30, R30
    
    moist_piv = moist_df[moist_df['angle'].isin(['L30', 'R30'])].pivot_table(index='sub_id', columns='angle', values='value')
    # Use available angles (if one missing, use the other, if both missing drop)
    moist_piv['avg'] = moist_piv.mean(axis=1)
    
    if not moist_piv.empty:
        vals = moist_piv['avg'].dropna().values
        # Invert Logic: High Raw = Good Moisture. Low Raw = Bad Moisture.
        # User wants High Score = Bad.
        # So Score base should be related to (Max - Raw).
        # Let's look at DISTRIBUTION of Raw Values first.
        
        mu, std = np.mean(vals), np.std(vals)
        print(f"Raw Value Dist: Mean={mu:.2f}, Std={std:.2f} (Min={vals.min():.2f}, Max={vals.max():.2f})")
        print("Proposed Grading (High Score = Dry = Bad):")
        # Grade 1 (Best/Moist) -> Raw Value High -> > Mean + 1.5 Std ?
        # Grade 5 (Worst/Dry) -> Raw Value Low
        
        # Percentile based thresholds
        p20, p40, p60, p80 = np.percentile(vals, [20, 40, 60, 80])
        print(f"Percentiles (20,40,60,80): {p20:.1f}, {p40:.1f}, {p60:.1f}, {p80:.1f}")
        
        print("Draft Thresholds (For Grade 1-5):")
        print(f"Grade 5 (Very Dry):  < {p20:.1f}")
        print(f"Grade 4 (Dry):       {p20:.1f} ~ {p40:.1f}")
        print(f"Grade 3 (Normal):    {p40:.1f} ~ {p60:.1f}")
        print(f"Grade 2 (Moist):     {p60:.1f} ~ {p80:.1f}")
        print(f"Grade 1 (Very Moist): > {p80:.1f}")

    # 2. Pore (Regression, L30/R30 Avg)
    print("\n--- PORE (L30/R30 Avg) ---")
    pore_df = reg_df[reg_df['attribute'] == 'cheek_pore']
    pore_piv = pore_df[pore_df['angle'].isin(['L30', 'R30'])].pivot_table(index='sub_id', columns='angle', values='value')
    pore_piv['avg'] = pore_piv.mean(axis=1)
    
    if not pore_piv.empty:
        vals = pore_piv['avg'].dropna().values
        # Raw: High = Many Pores = Bad
        mu, std = np.mean(vals), np.std(vals)
        print(f"Raw Value Dist: Mean={mu:.2f}, Std={std:.2f}")
        p20, p40, p60, p80 = np.percentile(vals, [20, 40, 60, 80])
        print(f"Percentiles: {p20:.1f}, {p40:.1f}, {p80:.1f}")
        
        print("Draft Thresholds:")
        print(f"Grade 1 (Clean):     < {p20:.1f}")
        print(f"Grade 2:             {p20:.1f} ~ {p40:.1f}")
        print(f"Grade 5 (Bad):       > {p80:.1f}")

    # 3. Wrinkle (Eye Reg + Forehead Class + Glabellus Class)
    print("\n--- WRINKLE (Composite) ---")
    # Eye (Reg, R30)
    eye_df = reg_df[(reg_df['attribute'] == 'perocular_wrinkle_Ra') & (reg_df['angle'] == 'R30')]
    eye_vals = eye_df.set_index('sub_id')['value']
    
    # Forehead (Class, L30/R30 avg -> round?)
    fh_df = cls_df[cls_df['attribute'].str.contains('forehead_wrinkle')]
    fh_agg = fh_df.groupby('sub_id')['value'].mean()
    
    # Glabellus (Class, R30?)
    gl_df = cls_df[cls_df['attribute'].str.contains('glabellus_wrinkle')]
    gl_agg = gl_df.groupby('sub_id')['value'].mean()
    
    # Merge
    wrinkle_merged = pd.concat([eye_vals, fh_agg, gl_agg], axis=1, keys=['eye', 'forehead', 'glabellus'])
    
    # Just check Eye distribution first since it's the main Regression component
    if not eye_vals.empty:
        vals = eye_vals.values
        print(f"[Eye Ra Only] Mean={np.mean(vals):.2f}, Std={np.std(vals):.2f}")
        p20, p40, p60, p80 = np.percentile(vals, [20, 40, 60, 80])
        print(f"Percentiles: {p20:.1f}, {p40:.1f}, {p80:.1f}")
        print(f"Grade 1 (No Wrinkle): < {p20:.1f}")
        print(f"Grade 5 (Deep):       > {p80:.1f}")

    # 4. Pigmentation (Regression, L30/R30 Avg)
    print("\n--- PIGMENTATION (L30/R30 Avg) ---")
    pig_df = reg_df[reg_df['attribute'] == 'pigmentation']
    pig_piv = pig_df[pig_df['angle'].isin(['L30', 'R30'])].pivot_table(index='sub_id', columns='angle', values='value')
    pig_piv['avg'] = pig_piv.mean(axis=1)
    
    if not pig_piv.empty:
        vals = pig_piv['avg'].dropna().values
        mu, std = np.mean(vals), np.std(vals)
        print(f"Raw Dist: Mean={mu:.2f}, Std={std:.2f}")
        p20, p40, p60, p80 = np.percentile(vals, [20, 40, 60, 80])
        print(f"Grade 1 (Clean): < {p20:.1f}")
        print(f"Grade 5 (Bad):   > {p80:.1f}")

    # 5. Sagging (Class, R30)
    print("\n--- SAGGING (Class R30) ---")
    sag_df = cls_df[(cls_df['attribute'].str.contains('sagging')) & (cls_df['angle'] == 'R30')]
    if not sag_df.empty:
        counts = sag_df['value'].value_counts().sort_index()
        total = len(sag_df)
        print("Class Distribution:")
        for cls, cnt in counts.items():
            print(f"Class {int(cls)}: {cnt} ({cnt/total*100:.1f}%)")

if __name__ == "__main__":
    analyze_and_propose()
