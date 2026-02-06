import csv
import sys
import statistics

def get_stats(data):
    if not data:
        return None
    return {
        'min': min(data),
        'max': max(data),
        'mean': statistics.mean(data),
        'std': statistics.stdev(data) if len(data) > 1 else 0
    }

def main():
    path = 'dataset/measurement_data.csv'
    encodings = ['utf-8', 'cp949', 'euc-kr']
    
    rows = []
    header = []
    
    for enc in encodings:
        try:
            with open(path, 'r', encoding=enc) as f:
                reader = csv.reader(f)
                header = next(reader)
                rows = list(reader)
            print(f"Successfully read with encoding: {enc}")
            break
        except UnicodeDecodeError:
            continue
        except Exception as e:
            print(f"Error reading file with {enc}: {e}")
            return

    if not rows:
        print("Failed to read data or empty file")
        return

    # Identify columns
    # Moisture: 수분_왼쪽볼, 수분_오른쪽볼
    # Pore: 모공개수_왼쪽볼, 모공개수_오른쪽볼
    # Wrinkle: 주름_왼쪽눈가_Ra, 주름_오른쪽눈가_Ra
    # Pigmentation: 스팟개수_정면
    
    targets = {
        'Moisture_L': '수분_왼쪽볼',
        'Moisture_R': '수분_오른쪽볼',
        'Pore_L': '모공개수_왼쪽볼',
        'Pore_R': '모공개수_오른쪽볼',
        'Wrinkle_L_Ra': '주름_왼쪽눈가_Ra',
        'Wrinkle_R_Ra': '주름_오른쪽눈가_Ra',
        'Pigmentation_F': '스팟개수_정면'
    }
    
    col_indices = {}
    for k, v in targets.items():
        try:
            col_indices[k] = header.index(v)
        except ValueError:
            print(f"Column not found: {v}")

    collected_data = {k: [] for k in targets.keys()}

    for row in rows:
        for k, idx in col_indices.items():
            if idx < len(row):
                try:
                    val = float(row[idx])
                    collected_data[k].append(val)
                except ValueError:
                    continue

    print("-" * 50)
    print(f"{'Attribute':<20} | {'Min':<10} | {'Max':<10} | {'Mean':<10} | {'Std':<10}")
    print("-" * 50)
    
    # Merge L/R for summary if needed, but showing individual first
    for k, v in collected_data.items():
        stats = get_stats(v)
        if stats:
             print(f"{k:<20} | {stats['min']:<10.2f} | {stats['max']:<10.2f} | {stats['mean']:<10.2f} | {stats['std']:<10.2f}")
    
    # Combined Stats
    print("-" * 50)
    combined_groups = {
        'Moisture': ['Moisture_L', 'Moisture_R'],
        'Pore': ['Pore_L', 'Pore_R'],
        'Wrinkle_Ra': ['Wrinkle_L_Ra', 'Wrinkle_R_Ra']
    }
    
    for group, keys in combined_groups.items():
        all_vals = []
        for k in keys:
            all_vals.extend(collected_data[k])
        stats = get_stats(all_vals)
        if stats:
             print(f"{group:<20} | {stats['min']:<10.2f} | {stats['max']:<10.2f} | {stats['mean']:<10.2f} | {stats['std']:<10.2f}")

if __name__ == "__main__":
    main()
