import os
import sys
import argparse
import pandas as pd
import numpy as np
import torch
import torch.nn as nn
from torch.utils.data import Dataset, DataLoader, WeightedRandomSampler
from torchvision import transforms
from PIL import Image
import timm
import wandb
from tqdm import tqdm

# Add parent directory to path to import core modules
workspace_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, workspace_path)

from core.utils import fix_seed
from core.logger import setup_logger

# Define Classes mapping based on CSV analysis
# ['복합건성' '중성' '복합지성' '건성' '지성' '심한건성']
CLASS_MAP = {
    '건성': 0,
    '중성': 1,
    '지성': 2,
    '복합건성': 3,
    '복합지성': 4,
    '심한건성': 5  # Note: Very few samples might exist for this
}

REV_CLASS_MAP = {v: k for k, v in CLASS_MAP.items()}

class SkinTypeDataset(Dataset):
    def __init__(self, metadata_path, img_root, transform=None, mode='train', seed=42):
        self.img_root = img_root
        self.transform = transform
        self.mode = mode
        self.data_list = []
        
        # Read CSV
        try:
            df = pd.read_csv(metadata_path, encoding='utf-8')
        except UnicodeDecodeError:
            df = pd.read_csv(metadata_path, encoding='cp949')
            
        print(f"Loaded Metadata: {len(df)} subjects")
        
        # Filter valid subjects (those capable of being loaded)
        valid_subjects = []
        for idx, row in df.iterrows():
            sub_no = int(row['subject_no'])
            label_str = row['얼굴피부타입']
            
            if label_str not in CLASS_MAP:
                continue
                
            # Check if image dir exists (Equipment 01 - Digital Camera)
            # Folder format: 4 digits, e.g., 0001
            sub_dir = f"{sub_no:04d}"
            sub_path = os.path.join(img_root, "01", sub_dir) # Using Equ 01
            
            if os.path.isdir(sub_path):
                valid_subjects.append({
                    "subject_no": sub_no,
                    "label": CLASS_MAP[label_str],
                    "path": sub_path
                })
        
        # Split Train/Val/Test by Subject ID to prevent leakage
        # Simple random split: 80% Train, 10% Val, 10% Test
        np.random.seed(seed)
        np.random.shuffle(valid_subjects)
        
        n_total = len(valid_subjects)
        n_train = int(n_total * 0.8)
        n_val = int(n_total * 0.1)
        
        if mode == 'train':
            self.subjects = valid_subjects[:n_train]
        elif mode == 'val':
            self.subjects = valid_subjects[n_train:n_train+n_val]
        else: # test
            self.subjects = valid_subjects[n_train+n_val:]
            
        print(f"[{mode.upper()}] Subjects: {len(self.subjects)}")
        
        # Flatten images
        # For each subject, take all Front/Side images?
        # Let's take 'F', 'L30', 'R30' as they are standard views
        target_angles = ['F', 'L30', 'R30', 'L15', 'R15'] 
        
        self.samples = []
        class_counts = [0] * len(CLASS_MAP)
        
        for sub in self.subjects:
            folder = sub['path']
            label = sub['label']
            files = os.listdir(folder)
            
            for fname in files:
                if not fname.lower().endswith(('.jpg', '.jpeg', '.png')):
                    continue
                
                # Check angle
                # format: 0001_01_F.jpg
                # parts: [id, equ, angle]
                parts = fname.rsplit('.', 1)[0].split('_')
                if len(parts) >= 3:
                    angle = parts[2]
                    if angle in target_angles:
                        self.samples.append((os.path.join(folder, fname), label))
                        class_counts[label] += 1
                        
        self.class_counts = class_counts
        print(f"[{mode.upper()}] Total Images: {len(self.samples)}")
        if mode == 'train':
            print(f"Class Distribution: {dict(zip(CLASS_MAP.keys(), class_counts))}")

    def __len__(self):
        return len(self.samples)

    def __getitem__(self, idx):
        path, label = self.samples[idx]
        
        try:
            img = Image.open(path).convert('RGB')
            if self.transform:
                img = self.transform(img)
            return img, label
        except Exception as e:
            print(f"Error loading {path}: {e}")
            # Return dummy on failure to prevent crash
            return torch.zeros((3, 224, 224)), label

def get_transforms(is_train=True, res=224):
    if is_train:
        return transforms.Compose([
            transforms.Resize((res, res)),
            transforms.RandomHorizontalFlip(),
            transforms.ColorJitter(brightness=0.1, contrast=0.1),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])
    else:
        return transforms.Compose([
            transforms.Resize((res, res)),
            transforms.ToTensor(),
            transforms.Normalize([0.485, 0.456, 0.406], [0.229, 0.224, 0.225])
        ])

def train(args):
    fix_seed(args.seed)
    
    save_dir = os.path.join("checkpoint", "skintype", args.name)
    os.makedirs(save_dir, exist_ok=True)
    
    logger = setup_logger("train_skintype", save_dir)
    logger.info(f"Args: {args}")
    
    # Dataset
    data_root = os.path.join(workspace_path, "dataset", "img")
    meta_path = os.path.join(workspace_path, "dataset", "meta_data.csv")
    
    train_ds = SkinTypeDataset(meta_path, data_root, transform=get_transforms(True, args.res), mode='train', seed=args.seed)
    val_ds = SkinTypeDataset(meta_path, data_root, transform=get_transforms(False, args.res), mode='val', seed=args.seed)
    
    # Weighted Sampler for Class Imbalance
    if args.use_sampler:
        num_samples = len(train_ds)
        labels = [s[1] for s in train_ds.samples]
        class_counts = train_ds.class_counts
        
        # Handle 0 counts (if any class missing)
        class_weights = [1.0 / c if c > 0 else 0.0 for c in class_counts]
        sample_weights = [class_weights[l] for l in labels]
        
        sampler = WeightedRandomSampler(sample_weights, num_samples, replacement=True)
        train_loader = DataLoader(train_ds, batch_size=args.batch_size, sampler=sampler, num_workers=4)
    else:
        train_loader = DataLoader(train_ds, batch_size=args.batch_size, shuffle=True, num_workers=4)
        
    val_loader = DataLoader(val_ds, batch_size=args.batch_size, shuffle=False, num_workers=4)
    
    # Model
    print(f"Creating model: {args.model_name}")
    num_classes = len(CLASS_MAP)
    model = timm.create_model(args.model_name, pretrained=True, num_classes=num_classes)
    model = model.cuda()
    
    # Optimizer
    optimizer = torch.optim.AdamW(model.parameters(), lr=args.lr, weight_decay=1e-4) # AdamW typically better for transformers
    criterion = nn.CrossEntropyLoss()
    
    scheduler = torch.optim.lr_scheduler.CosineAnnealingLR(optimizer, T_max=args.epoch)
    
    # WandB
    if args.wandb:
        wandb.init(project=args.wandb_project, entity=args.wandb_entity, name=f"skintype_{args.name}", config=vars(args))
    
    best_acc = 0.0
    
    for epoch in range(args.epoch):
        model.train()
        train_loss = 0
        correct = 0
        total = 0
        
        pbar = tqdm(train_loader, desc=f"Epoch {epoch+1}/{args.epoch} [Train]")
        for img, label in pbar:
            img, label = img.cuda(), label.cuda()
            
            optimizer.zero_grad()
            out = model(img)
            loss = criterion(out, label)
            loss.backward()
            optimizer.step()
            
            train_loss += loss.item() * img.size(0)
            _, pred = out.max(1)
            correct += pred.eq(label).sum().item()
            total += img.size(0)
            
            pbar.set_postfix({'loss': loss.item()})
            
        train_acc = correct / total
        train_loss = train_loss / total
        scheduler.step()
        
        # Validation
        model.eval()
        val_loss = 0
        val_correct = 0
        val_total = 0
        
        with torch.no_grad():
            for img, label in tqdm(val_loader, desc=f"Epoch {epoch+1}/{args.epoch} [Val]"):
                img, label = img.cuda(), label.cuda()
                out = model(img)
                loss = criterion(out, label)
                
                val_loss += loss.item() * img.size(0)
                _, pred = out.max(1)
                val_correct += pred.eq(label).sum().item()
                val_total += img.size(0)
                
        val_acc = val_correct / val_total
        val_loss = val_loss / val_total
        
        logger.info(f"Epoch {epoch+1}: Train Loss {train_loss:.4f} Acc {train_acc:.4f} | Val Loss {val_loss:.4f} Acc {val_acc:.4f}")
        
        if args.wandb:
            wandb.log({
                "train_loss": train_loss, "train_acc": train_acc,
                "val_loss": val_loss, "val_acc": val_acc,
                "epoch": epoch
            })
            
        if val_acc > best_acc:
            best_acc = val_acc
            save_path = os.path.join(save_dir, "best_model.pth")
            torch.save(model.state_dict(), save_path)
            logger.info(f"Saved Best Model: {best_acc:.4f}")

if __name__ == "__main__":
    parser = argparse.ArgumentParser()
    parser.add_argument("--name", type=str, default="exp_skintype_base")
    parser.add_argument("--epoch", type=int, default=30)
    parser.add_argument("--batch_size", type=int, default=32)
    parser.add_argument("--lr", type=float, default=1e-4)
    parser.add_argument("--model_name", type=str, default="coatnet_2_rw_224")
    parser.add_argument("--res", type=int, default=224)
    parser.add_argument("--seed", type=int, default=42)
    parser.add_argument("--use_sampler", action="store_true", help="Use weighted sampler for class imbalance")
    parser.add_argument("--wandb", action="store_true")
    parser.add_argument("--wandb_project", type=str, default="mento")
    parser.add_argument("--wandb_entity", type=str, default="kangseunghun9927-sw-")
    
    args = parser.parse_args()
    train(args)
