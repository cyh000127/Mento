import json
import os
import bisect

class SkinScorer:
    def __init__(self, stats_path=None):
        if stats_path is None:
            # tool/analysis/scoring.py -> tool/resources/reference_stats.json
            # .. -> tool
            stats_path = os.path.join(os.path.dirname(os.path.dirname(os.path.abspath(__file__))), "resources/reference_stats.json")
        
        self.stats = {}
        if os.path.exists(stats_path):
            with open(stats_path, "r") as f:
                self.stats = json.load(f)
        else:
            print(f"[WARNING] Reference stats not found at {stats_path}")

        # Configuration: Higher Value is Better?
        self.higher_is_better = {
            "pigmentation": False,
            "pore": False,
            "wrinkle": False, # Uses Ra or Grade (Lower is better)
            "moisture": True,
            "elasticity": True,
            "sagging": False # Assuming Grade 0 is best
        }
        
        # Cache for threshold lists to avoid rebuilding
        self.cache = {}

        # Value Scaling Factors (Model Output -> CSV Unit)
        self.scalers = {
            "pigmentation": 350.0,
            "pore": 2600.0,
            "moisture": 100.0, # Model 0-1 matches 0-100? Reg moisture output range check needed. 1_trainset_info says /100.
            "elasticity": 1.0, # Check scaling
            "wrinkle": 1.0 # Class output is Grade. 
        }
        
    def get_real_value(self, category, model_value):
        """Denormalizes model output to real unit value."""
        if category == "wrinkle":
            # Map Grade to Ra
            grade_to_ra = {
                0: 9.0, 1: 15.0, 2: 19.0, 3: 23.0, 4: 27.0, 5: 31.0, 6: 35.0
            }
            return grade_to_ra.get(int(round(model_value)), 20.0)
            
        scaler = self.scalers.get(category, 1.0)
        return model_value * scaler

    def get_group_key(self, age, gender):
        if age is None or gender is None:
            return "global"
            
        try:
            age_int = int(age)
        except:
            return "global"
            
        # Determine Age Group
        if age_int < 20: age_grp = "10s"
        elif age_int < 30: age_grp = "20s"
        elif age_int < 40: age_grp = "30s"
        elif age_int < 50: age_grp = "40s"
        elif age_int < 60: age_grp = "50s"
        else: age_grp = "60s_plus"
        
        # Determine Gender
        g = str(gender).lower().strip()
        if g in ["male", "m", "남성", "남"]: gen_key = "male"
        elif g in ["female", "f", "여성", "여"]: gen_key = "female"
        else: return "global"
        
        return f"{gen_key}_{age_grp}"

    def _get_thresholds(self, group_key, category):
        """
        Retrieves or creates sorted threshold list for binary search.
        """
        cache_key = f"{group_key}_{category}"
        if cache_key in self.cache:
            return self.cache[cache_key]
            
        if group_key not in self.stats or category not in self.stats[group_key]:
            return None
            
        current_stats = self.stats[group_key][category]
        thresholds = []
        for i in range(101):
            thresholds.append(current_stats.get(str(i), 0))
            
        self.cache[cache_key] = thresholds
        return thresholds

    def get_percentile(self, category, value, age=None, gender=None):
        """
        Returns the percentile (0-100) of the value in the reference distribution.
        Uses Linear Interpolation for smoothness.
        """
        group_key = self.get_group_key(age, gender)
        
        # Fallback to global if group specific stats missing
        if group_key not in self.stats:
            group_key = "global"
            
        thresholds = self._get_thresholds(group_key, category)
        
        # Double fallback
        if thresholds is None:
            if "global" in self.stats:
                 thresholds = self._get_thresholds("global", category)
        
        if thresholds is None:
             return 50 # Default middle
             
        # Binary Search (bisect_left)
        # thresholds is sorted list of 101 values (p0 ... p100)
        idx = bisect.bisect_left(thresholds, value)
        
        # Exact match or out of bounds
        if idx == 0: return 0
        if idx >= 101: return 100
        
        # Linear Interpolation
        # value is between thresholds[idx-1] and thresholds[idx]
        # range spans percentile (idx-1) to (idx)
        val_low = thresholds[idx-1]
        val_high = thresholds[idx]
        
        if val_high == val_low:
            return idx
            
        fraction = (value - val_low) / (val_high - val_low)
        percentile = (idx - 1) + fraction
        
        return max(0, min(100, percentile))


    def calculate_score(self, category, model_value, age=None, gender=None):
        """
        Calculates final 0-100 score.
        High Score = Good Skin.
        """

        # 1. Scale Value
        real_value = self.get_real_value(category, model_value)

        # 2. Get Percentile in Population
        # Note: Stats keys might differ from simple category names.
        stats_key = category
        if category == "wrinkle": stats_key = "wrinkle_ra" # Use Ra stats

        p = self.get_percentile(stats_key, real_value, age, gender)
        
        # 3. Convert to Score (High Score = Good)
        is_higher_better = self.higher_is_better.get(category, False)
        
        if is_higher_better:
            score = p # Top 99% moisture = Score 99
        else:
            score = 100 - p # Top 99% spots (High count) = Score 1
            
        return score

if __name__ == "__main__":
    scorer = SkinScorer()
    print("Test Scores (Global vs Segmented):")
    val_pig = 0.5 # 175 spots
    print(f"Pigmentation (175 spots) [Global]: {scorer.calculate_score('pigmentation', val_pig)}")
    print(f"Pigmentation (175 spots) [Female 20s]: {scorer.calculate_score('pigmentation', val_pig, 25, 'female')}")
    print(f"Pigmentation (175 spots) [Male 60s]: {scorer.calculate_score('pigmentation', val_pig, 65, 'male')}")

