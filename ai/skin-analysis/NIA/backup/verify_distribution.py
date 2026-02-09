import os
import pandas as pd
import numpy as np

# Define the proposed scoring logic
def calculate_score_grade(attribute, value, mode='score'):
    # value is the RAW value (predicted or ground truth)
    score = 0
    grade = 0
    
    if attribute == 'moisture':
        # Logic: 100 - value (Higher raw = better moisture = lower score)
        # Raw range: approx 20-100
        score = np.clip(100 - value, 0, 100)
        
    elif attribute == 'pore':
        # Logic: (value / 1800) * 100
        # Raw max approx 2500, but using 1800 as limit
        score = np.clip((value / 1800) * 100, 0, 100)
        
    elif attribute == 'wrinkle':
        # Logic: (value - 10) / 40 * 100
        # Raw range 10-50
        score = np.clip((value - 10) / 40 * 100, 0, 100)
        
    elif attribute == 'pigmentation':
        # Logic: (value / 300) * 100
        # Raw max 350, using 300 as limit
        score = np.clip((value / 300) * 100, 0, 100)
    
    elif attribute == 'sagging':
         # Class 0-5. Score = Class * 20
         # If value is raw class (0,1,2,3,4,5)
         score = np.clip(value * 20, 0, 100)

    # Grade Calculation
    if score <= 20: grade = 1
    elif score <= 40: grade = 2
    elif score <= 60: grade = 3
    elif score <= 80: grade = 4
    else: grade = 5
    
    return score, grade

def load_predictions(file_path):
    # Format: Angle, Attribute, Value, Filename
    # Example: F, pigmentation, 30.092, Sub_0304_Equ_01_Angle_F_Area_00
    data = []
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return data
        
    with open(file_path, 'r') as f:
        for line in f:
            parts = line.strip().split(',')
            if len(parts) >= 3:
                angle = parts[0].strip()
                attr = parts[1].strip()
                try:
                    val = float(parts[2].strip())
                    data.append({'angle': angle, 'attribute': attr, 'value': val})
                except ValueError:
                    continue
    return data

def analyze_distribution():
    print("=== Analyzing Distribution based on Logic ===")
    
    # 1. Load Regression Predictions
    reg_path = 'checkpoint/regression/1st_coat/prediction/pred.txt'
    reg_data = load_predictions(reg_path)
    
    # 2. Load Classification Predictions (Sagging/Wrinkle Class if available)
    # Asking user to check class prediction path later, for now we simulate sagging based on logic or skip
    # Assuming we focus on Regulation attributes first + Pigmentation
    
    df = pd.DataFrame(reg_data)
    
    if df.empty:
        print("No prediction data found.")
        return

    # Map attribute names from file to our logic names
    # File attr: pigmentation, cheek_moisture, forehead_moisture, etc.
    # Logic: moisture (avg), pore (avg), wrinkle (ra), pigmentation (avg)
    
    target_attrs = {
        'moisture': ['cheek_moisture', 'forehead_moisture', 'chin_moisture'], # Averaging indiscriminately for distribution check
        'pore': ['cheek_pore'], 
        'wrinkle': ['perocular_wrinkle_Ra'],
        'pigmentation': ['pigmentation', 'cheek_pigmentation', 'forehead_pigmentation']
    }
    
    results = {}
    
    for logic_name, file_attrs in target_attrs.items():
        # Filter data
        subset = df[df['attribute'].isin(file_attrs)].copy()
        
        if subset.empty:
            print(f"No data for {logic_name}")
            continue
            
        # apply logic
        scores = []
        grades = []
        
        for val in subset['value']:
            s, g = calculate_score_grade(logic_name, val)
            scores.append(s)
            grades.append(g)
            
        subset['score'] = scores
        subset['grade'] = grades
        
        print(f"\n[ {logic_name.upper()} ] (N={len(subset)})")
        print(f"Score Mean: {np.mean(scores):.2f}, Std: {np.std(scores):.2f}")
        
        # Grade Distribution
        grade_counts = subset['grade'].value_counts().sort_index()
        total = len(subset)
        
        for g in range(1, 6):
            count = grade_counts.get(g, 0)
            percent = (count / total) * 100
            bar = '#' * int(percent // 2)
            print(f"Grade {g}: {count:>4} ({percent:>5.1f}%) {bar}")

if __name__ == "__main__":
    analyze_distribution()
