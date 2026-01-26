import pandas as pd
import numpy as np

def load_data():
    measure_df = pd.read_csv("dataset/measurement_data.csv")
    meta_df = pd.read_csv("dataset/meta_data.csv")
    
    # Merge on subject_no
    df = pd.merge(measure_df, meta_df, on="subject_no")
    return df

def analyze_feature(df, feature_name, new_col_name):
    # Calculate global stats
    desc = df[feature_name].describe(percentiles=[0.1, 0.25, 0.5, 0.75, 0.9])
    
    # Calculate stats by age group
    # 20s, 30s, 40s, 50s, 60+
    df['age_group'] = (df['나이'] // 10) * 10
    age_stats = df.groupby('age_group')[feature_name].agg(['mean', 'std', 'min', 'max']).round(2)
    
    # Calculate stats by gender
    gender_stats = df.groupby('성별')[feature_name].agg(['mean', 'std', 'count']).round(2)
    
    return desc, age_stats, gender_stats

def main():
    df = load_data()
    print(f"Total Subjects: {len(df)}")
    print("\nGender Distribution:")
    print(df['성별'].value_counts())
    
    # Define mapping from Our Model Outputs to CSV Columns
    # Model Output -> CSV Column (Approximate)
    # Moisture: Cheek Moisture -> Average of Left/Right Cheek Moisture?
    df['mean_cheek_moisture'] = (df['수분_오른쪽볼'] + df['수분_왼쪽볼']) / 2
    
    # Pigmentation: No direct "Pigmentation" column in header shown? 
    # Found '스팟개수_정면'? Or maybe hidden columns?
    # Let's check columns for 'pigmentation' or 'spot' or 'melanin'
    # Actually, the header shown was: `스팟개수_정면`, `모공개수_오른쪽볼`, `모공개수_왼쪽볼`
    # Our Regression Model predicts: "Pigmentation" (likely count or density?), "Pore" (count?)
    
    # Pores
    df['mean_cheek_pore'] = (df['모공개수_오른쪽볼'] + df['모공개수_왼쪽볼']) / 2
    
    features = {
        "Moisture (Cheek)": "mean_cheek_moisture",
        "Pore Count (Cheek)": "mean_cheek_pore", 
        "Spot Count (Front)": "스팟개수_정면",  # Assuming this maps to Pigmentation
        "Elasticity R2 (Cheek)": "탄력_왼쪽볼_R2", # Using Left cheek as rep
        "Wrinkle Ra (Eye)": "주름_왼쪽눈가_Ra" # Using Left eye as rep
    }
    
    results = {}
    
    print("\n" + "="*50)
    print("EDA Report with Gender Analysis")
    print("="*50)

    for alias, col in features.items():
        if col not in df.columns:
            print(f"WARNING: Column {col} not found for {alias}")
            continue
            
        print(f"\nAnalysis for: {alias}")
        desc, age_stats, gender_stats = analyze_feature(df, col, alias)
        
        print("\n--- Global Percentiles ---")
        print(desc[['10%', '25%', '50%', '75%', '90%']])
        
        print("\n--- By Age Group ---")
        print(age_stats)
        
        print("\n--- By Gender ---")
        print(gender_stats)
        
        # Correlation with Age
        corr = df['나이'].corr(df[col])
        print(f"\nCorrelation with Age: {corr:.3f}")
        
    # Check if we should segment by Skin Type
    # print("\n--- By Skin Type ---")
    # print(df.groupby('얼굴피부타입')['mean_cheek_moisture'].mean())

if __name__ == "__main__":
    main()
