from collections import defaultdict
from copy import deepcopy
import math
import torch
import torch.nn as nn
from torch.nn.utils import clip_grad_norm_
import numpy as np
from scipy.stats import pearsonr
import glob
import shutil
import os

try:
    import wandb
except ImportError:
    wandb = None

from tqdm import tqdm
from core.data_loader import mkdir
import torch.optim as optim
from core.utils import (
    AverageMeter,
    mape_loss,
    CB_loss,
    save_checkpoint,
    CharbonnierLoss,
    FocalLoss,
    EarlyStopping  # Newly added
)
from sklearn.metrics import precision_recall_fscore_support, mean_absolute_error

if torch.cuda.is_available():
    device = torch.device("cuda")
else:
    device = torch.device("cpu")


class Model(object):
    def __init__(self, **kwargs):
        for key, value in kwargs.items():
            setattr(self, key, value)

        self.train_loss, self.val_loss = AverageMeter(), AverageMeter()
        
        # Initialize Metrics Logic
        self.initialize_metrics()

        self.epoch = 0
        self.nan = 0
        self.phase, self.device = None, device
        self.pred, self.gt = list(), list()
        self.pred_t, self.gt_t = list(), list()

        # Optimizer
        self.optimizer = torch.optim.AdamW(
            params=self.model.parameters(),
            lr=self.args.lr,
            weight_decay=1e-4, # Added weight decay for regularization
        )
        self.grad_accum_steps = max(1, int(getattr(self.args, "grad_accum_steps", 1)))

        # Robust Scheduler: ReduceLROnPlateau
        # This reduces LR when a metric has stopped improving.
        self.scheduler = optim.lr_scheduler.ReduceLROnPlateau(
            self.optimizer, mode='min', factor=0.5, patience=3, verbose=True
        )

        # Early Stopping
        # We manually handle 'save_checkpoint' logic via the Model class, 
        # but we use EarlyStopping to track patience.
        # However, since EarlyStopping class in utils expects to save itself, 
        # we will use a custom simple logic here or adapt.
        # Let's implement robust tracking internally using the requested best practices.
        self.stop_early_counter = 0
        self.best_score = None
        self.early_stop = False
        self.patience = getattr(self.args, "stop_early", 10)

        self._norm_mean = torch.tensor([0.485, 0.456, 0.406], device=self.device).view(1, 3, 1, 1)
        self._norm_std = torch.tensor([0.229, 0.224, 0.225], device=self.device).view(1, 3, 1, 1)

    def initialize_metrics(self):
        # Tracking dictionaries
        self.best_loss = getattr(self.args, "best_loss", {})
        self.load_epoch = getattr(self.args, "load_epoch", {})
        
        # Ensure keys exist
        if self.m_dig not in self.best_loss:
            self.best_loss[self.m_dig] = np.inf

    def _denormalize_for_logging(self, tensor: torch.Tensor) -> torch.Tensor:
        if tensor.dim() == 3:
            tensor = tensor.unsqueeze(0)
        img = tensor.detach()
        img = img * self._norm_std.to(img.device) + self._norm_mean.to(img.device)
        return img.clamp(0.0, 1.0).squeeze(0)

    def _handle_non_finite_loss(self, loss, pred, label):
        self.nan += 1
        self.logger.warning(f"[{self.phase}] Non-finite loss detected: {loss.item()}")

    def reset_log(self):
        self.train_loss.reset()
        self.val_loss.reset()
        self.epoch += 1
        self.pred, self.gt = [], []
        self.pred_t, self.gt_t = [], []

    def update_e(self, epoch, **kwargs):
        self.epoch = self.best_epoch = epoch
        for key, value in kwargs.items():
            setattr(self, key, value)

    # =========================================================================
    # Training Loop
    # =========================================================================
    def train(self):
        self.phase = "Train"
        self.model.train()
        
        # Loss Function Selection
        if self.args.mode == "class":
            self.criterion = CB_loss(
                samples_per_cls=self.grade_num,
                no_of_classes=len(self.grade_num),
                gamma=self.args.gamma,
            )
        else:
            self.criterion = nn.HuberLoss()

        accum_steps = self.grad_accum_steps
        accum_counter = 0
        self.optimizer.zero_grad()

        loop = tqdm(self.train_loader, desc=f"Epoch {self.epoch} [Train]")
        
        for self.iter, (img, label, self.img_names, _, _, _) in enumerate(loop):
            img, label = img.to(device), label.to(device)

            pred = self.model(img)

            if self.args.mode == "class":
                loss = self.class_loss(pred, label)
            else:
                loss = self.regression(pred, label)

            if loss is None:
                self.optimizer.zero_grad(set_to_none=True)
                accum_counter = 0
                continue
            
            # Logging First Batch Images
            if self.wandb_run and self.iter == 0 and img.size(0) > 0:
                self._log_images(img, pred, label)

            # Gradient Accumulation
            loss_to_backprop = loss / accum_steps
            loss_to_backprop.backward()
            accum_counter += 1

            if accum_counter == accum_steps or (self.iter + 1) == len(self.train_loader):
                # Gradient Clipping
                if getattr(self.args, "grad_clip", 1.0) > 0:
                    clip_grad_norm_(self.model.parameters(), max_norm=self.args.grad_clip)
                
                self.optimizer.step()
                self.optimizer.zero_grad()
                self.global_step += 1
                accum_counter = 0
            
            # Progress Bar Update
            loop.set_postfix(loss=self.train_loss.avg)

        self.print_summary(final=True)

    # =========================================================================
    # Validation Loop
    # =========================================================================
    def valid(self):
        self.phase = "Valid"
        self.model.eval()
        
        if self.args.mode == "class":
            self.criterion = nn.CrossEntropyLoss()
        else:
            self.criterion = nn.L1Loss()

        with torch.no_grad():
            loop = tqdm(self.valid_loader, desc=f"Epoch {self.epoch} [Valid]")
            
            for self.iter, (img, label, self.img_names, _, _, _) in enumerate(loop):
                img, label = img.to(device), label.to(device)
                pred = self.model(img)

                if self.args.mode == "class":
                    loss = self.class_loss(pred, label)
                else:
                    loss = self.regression(pred, label)
                
                if loss is not None:
                    loop.set_postfix(loss=self.val_loss.avg)
            
            # Log Validation Images
            if self.wandb_run and img.size(0) > 0:
                 self._log_images(img, pred, label, title="valid/image")

        # Scheduler Step
        # ReduceLROnPlateau requires a metric (val_loss)
        self.scheduler.step(self.val_loss.avg)
        
        # Checkpoint & Early Stopping Logic
        self.check_improvement()
        
        self.print_summary(final=True)

    def _log_images(self, img, pred, label, title="train/image"):
        preview = []
        limit = min(3, img.size(0))
        for i in range(limit):
            vis_img = self._denormalize_for_logging(img[i]).cpu()
            if self.args.mode == "class":
                p_val = pred[i].argmax().item()
            else:
                p_val = round(pred[i].item(), 4)
                
            caption = f"GT: {label[i].item()}, Pred: {p_val}, Name: {self.img_names[i]}"
            preview.append(wandb.Image(vis_img, caption=caption))
        self.wandb_run.log({title: preview}, step=self.global_step)

    def check_improvement(self):
        """Robust Early Stopping & Checkpoint Saving"""
        current_loss = self.val_loss.avg
        score = -current_loss
        
        if self.best_score is None:
            self.best_score = score
            self._save_checkpoint()
        elif score < self.best_score:
            # No improvement
            self.stop_early_counter += 1
            self.logger.info(f"EarlyStopping counter: {self.stop_early_counter} out of {self.patience}")
            if self.stop_early_counter >= self.patience:
                self.early_stop = True
        else:
            # Improved
            self.best_score = score
            self._save_checkpoint()
            self.stop_early_counter = 0 # Reset counter
    
    def _save_checkpoint(self):
        # Helper to gather metrics for saving
        # Logic adapted from legacy save_checkpoint usage
        # We need to compute metrics first if not already available in self
        # Legacy code computed them in print_loss(final=True). 
        # Here we assume we can pass None if not computed or compute them.
        
        # Simple computation for legacy compatibility:
        correct_, all_ = defaultdict(int), defaultdict(int)
        micro_precision, correlation = 0.0, 0.0
        
        if self.args.mode == "class":
             # Compute standard metrics
             f_pred, f_gt = [], []
             for p_list, g_list in zip(self.pred, self.gt):
                 for p, g in zip(p_list, g_list):
                     f_pred.append(p)
                     f_gt.append(g)
                     all_[g] += 1
                     if g == p: correct_[g] += 1
             
             if len(f_gt) > 0:
                 micro_precision, _, _, _ = precision_recall_fscore_support(f_gt, f_pred, average="micro", zero_division=0)
                 # Pearson might fail for classification integers but we try
                 try:
                     correlation, _ = pearsonr(f_gt, f_pred)
                 except:
                     correlation = 0
                     
        else: # Regression
            # Compute Correlation
            f_pred = [val for sublist in self.pred for val in sublist]
            f_gt = [val for sublist in self.gt for val in sublist]
            if len(f_gt) > 1:
                try:
                    correlation, _ = pearsonr(f_gt, f_pred)
                except:
                    correlation = 0
        
        self.best_loss[self.m_dig] = round(self.val_loss.avg, 4)
        save_checkpoint(self, correct_, all_, micro_precision, correlation)
        self.logger.info(f"Saved Best Model: Loss {self.best_loss[self.m_dig]:.4f}")

    def stop_early(self):
        if self.early_stop:
            self.logger.info("Early stopping triggered.")
            # Create 'done' marker
            mkdir(os.path.join("checkpoint", self.args.mode, self.args.name, "save_model", str(self.m_dig), "done"))
            return True
        return False

    def print_summary(self, final=False):
        # Simplified logging wrapper
        loss = self.train_loss.avg if self.phase == "Train" else self.val_loss.avg
        if final:
            self.logger.info(f"Epoch {self.epoch} [{self.phase}] Loss: {loss:.4f}")
            if self.wandb_run:
                self.wandb_run.log({f"{self.phase.lower()}_loss": loss, "epoch": self.epoch}, step=self.global_step)

    # =========================================================================
    # Helpers (Legacy Compatibility)
    # =========================================================================
    def class_loss(self, pred, label):
        loss = self.criterion(pred, label)
        if hasattr(loss, "mean"): loss = loss.mean()
        
        if not torch.isfinite(loss):
             self._handle_non_finite_loss(loss, pred, label)
             return None
             
        self._update_metrics(loss, pred, label)
        return loss

    def regression(self, pred, label):
        pred = pred.flatten()
        loss = self.criterion(pred.float(), label.float())
        if not torch.isfinite(loss):
             self._handle_non_finite_loss(loss, pred, label)
             return None
        self._update_metrics(loss, pred, label)
        return loss

    def _update_metrics(self, loss, pred, label):
        bs = pred.shape[0] if pred.dim() > 0 else 1
        with torch.no_grad():
            if self.phase == "Train":
                self.train_loss.update(loss.item(), bs)
            else:
                self.val_loss.update(loss.item(), bs)
                
            # Store predictions for metrics
            if self.args.mode == "class":
                p_v = [p.argmax().item() for p in pred]
                g_v = [g.item() for g in label]
            else:
                p_v = [p.item() for p in pred]
                g_v = [g.item() for g in label]
                
            if self.phase == "Train":
                self.pred_t.append(p_v)
                self.gt_t.append(g_v)
            else:
                self.pred.append(p_v)
                self.gt.append(g_v)
    
    def print_best(self):
        # Legacy stub
        pass

# Add Model_test class for compatibility if needed (Assuming it inherits and modifies test logic)

class Model_test(Model):
    def __init__(self, args, logger):
        self.args = args
        self.pred = defaultdict(lambda: defaultdict(list))
        self.gt = defaultdict(lambda: defaultdict(list))
        self.logger = logger
        self._save_path = None
        self._log_files_reset = False
        self.wandb_run = None
        self.device = torch.device("cuda" if torch.cuda.is_available() else "cpu")

    def _ensure_save_path(self):
        if self._save_path is None:
            self._save_path = os.path.join(
                self.args.log_path,
                "save-log",
            )
        mkdir(self._save_path)
        self._reset_log_files(self._save_path)
        return self._save_path

    def _reset_log_files(self, save_path: str):
        """Prevent appending to old evaluation logs by clearing them once per run."""
        if self._log_files_reset:
            return

        if os.path.isdir(save_path):
            try:
                shutil.rmtree(save_path)
            except OSError as exc:
                self.logger.warning(f"Failed to clear old log directory {save_path}: {exc}")
        mkdir(save_path)

        for pattern in ("print_*.txt", "print_total.txt"):
            for file_path in glob.glob(os.path.join(save_path, pattern)):
                try:
                    os.remove(file_path)
                except OSError as exc:
                    self.logger.warning(f"Failed to remove old log file {file_path}: {exc}")
        self._log_files_reset = True

    def test(self, model, testset_loader, key):
        self.model = model
        self.testset_loader = testset_loader
        self.m_dig = key
        with torch.no_grad():
            self.model.eval()
            for self.iter, (img, label, self.img_names, self.digs, _, _) in enumerate(
                tqdm(self.testset_loader, desc=self.m_dig)
            ):
                img, label = img.to(self.device), label.to(self.device)
                pred = self.model.to(self.device)(img)

                if self.args.mode == "class":
                    self.get_test_acc(pred, label)
                else:
                    self.get_test_loss(pred, label)

    def save_value(self):
        pred_path = os.path.join(
            self.args.check_root,
            "checkpoint",
            self.args.mode,
            self.args.name,
            "prediction",
        )
        mkdir(pred_path)

        with open(os.path.join(pred_path, f"pred.txt"), "w") as p:
            with open(os.path.join(pred_path, f"gt.txt"), "w") as g:
                for key in list(self.pred.keys()):
                    for angle in sorted(self.pred[key].keys()):
                        for p_v, g_v in zip(self.pred[key][angle], self.gt[key][angle]):
                            p.write(f"{angle}, {key}, {p_v[0]}, {p_v[1]} \n")
                            g.write(f"{angle}, {key}, {g_v[0]}, {g_v[1]} \n")
        g.close()
        p.close()

    def print_test(self):
        pred_total, gt_total = list(), list()
        for self.angle in sorted(self.pred[self.m_dig].keys()):
            gt_v = [value[0] for value in self.gt[self.m_dig][self.angle]]
            pred_v = [value[0] for value in self.pred[self.m_dig][self.angle]]

            pred_total.append(pred_v)
            gt_total.append(gt_v)
            self.print_maes(gt_v, pred_v, True)

        gt_v = [j for i in gt_total for j in i]
        pred_v = [j for i in pred_total for j in i]
        self.print_maes(gt_v, pred_v, False)

    def get_test_loss(self, pred, gt):
        if "elasticity_R2" in self.m_dig:
            value = 1
        elif "moisture" in self.m_dig:
            value = 100
        elif "wrinkle_Ra" in self.m_dig:
            value = 50
        elif self.m_dig == "pigmentation":
            value = 350
        elif "pore" in self.m_dig:
            value = 2600
        else:
            assert 0, "error"

        for idx, (pred_item, gt_item) in enumerate(zip(pred, gt)):
            self.pred[self.m_dig][self.img_names[idx].split("_")[-3]].append(
                [round(pred_item.item() * value, 3), self.img_names[idx], value]
            )
            self.gt[self.m_dig][self.img_names[idx].split("_")[-3]].append(
                [round(gt_item.item() * value, 3), self.img_names[idx], value]
            )

    def get_test_acc(self, pred, gt):
        for idx, (pred_item, gt_item) in enumerate(zip(pred, gt)):
            self.pred[self.m_dig][self.img_names[idx].split("_")[-3]].append(
                [pred_item.argmax().item(), self.img_names[idx]]
            )
            self.gt[self.m_dig][self.img_names[idx].split("_")[-3]].append(
                [gt_item.item(), self.img_names[idx]]
            )

    def print_maes(self, gt_v, pred_v, angle):
        correct_ = defaultdict(int)
        all_ = defaultdict(int)

        for gt, pred in zip(gt_v, pred_v):
            all_[gt] += 1
            if gt == pred:
                correct_[gt] += 1

        try:
            if len(gt_v) > 1 and np.std(gt_v) > 0 and np.std(pred_v) > 0:
                correlation, p_value = pearsonr(gt_v, pred_v)
            else:
                correlation = float("nan")
                p_value = float("nan")
                self.logger.warning(
                    f"Pearson correlation not defined for {self.m_dig}"
                )
        except Exception as e:
            correlation = float("nan")
            p_value = float("nan")
            self.logger.warning(f"Pearsonr failed for {self.m_dig}: {e}")
            
        save_path = self._ensure_save_path()

        if self.args.mode == "regression":
            n_gt_v = [value / max(gt_v) for value in gt_v]
            n_pred_v = [value / max(pred_v) for value in pred_v]

            mae = mean_absolute_error(gt_v, pred_v)
            mape = mape_loss()(np.array(pred_v), np.array(gt_v))
            nmae = mean_absolute_error(n_gt_v, n_pred_v)

            if angle:
                self.logger.info(
                    f"[{self.angle}][{self.m_dig}]Correlation: {correlation:.2f}, P-value: {p_value:.4f}, MAE: {mae:.4f}, MAPE: {mape:.3f}, NMAE: {nmae:.3f}"
                )

                file_path = os.path.join(save_path, f"print_{self.angle}.txt")
                file_exists = os.path.exists(file_path)
                with open(file_path, "a") as f:
                    if self.m_dig == "pigmentation" and not file_exists:
                        f.write(f"Angle, Area, Correlation, P-value, MAE, MAPE, NMAE\n")
                    f.write(
                        f"{self.angle}, {self.m_dig}, {correlation:.2f}, {p_value:.4f}, {mae:.2f}, {mape:.2f}, {nmae:.2f}\n"
                    )

            else:
                if self.wandb_run is not None:
                     self.wandb_run.log({
                        f"Correlation/Total": correlation,
                        f"MAE/Total": mae,
                        f"MAPE/Total": mape,
                        f"NMAE/Total": nmae
                    })
                
                file_path = os.path.join(save_path, "print_total.txt")
                file_exists = os.path.exists(file_path)
                with open(file_path, "a") as f:
                    if self.m_dig == "pigmentation" and not file_exists:
                        f.write(f"Area, Correlation, P-value, MAE, MAPE, NMAE\n")
                    f.write(
                        f"{self.m_dig}, {correlation:.2f}, {p_value:.4f}, {mae:.2f}, {mape:.2f}, {nmae:.2f}\n"
                    )

        else:
            mae_ = [abs(p - g) for p, g in zip(pred_v, gt_v)]
            mae_ = sum(mae_) / len(mae_)

            mae_0 = [True if abs(p - g) == 0 else False for p, g in zip(pred_v, gt_v)]
            mae_0 = sum(mae_0) / len(mae_0)

            mae_1 = [True if abs(p - g) <= 1 else False for p, g in zip(pred_v, gt_v)]
            mae_1 = sum(mae_1) / len(mae_1)

            mae_2 = [True if abs(p - g) <= 2 else False for p, g in zip(pred_v, gt_v)]
            mae_2 = sum(mae_2) / len(mae_2)

            if angle:
                self.logger.info(
                    f"[{self.angle}][{self.m_dig}]Correlation: {correlation:.2f}, P-value: {p_value:.4f}, MAE: {mae_:.2f}, MAE(==0): {mae_0 * 100:.2f}%,  MAE(=<1): {mae_1 * 100:.2f}%, MAE(=<2): {mae_2 * 100:.2f}%"
                )
                for grade in all_:
                    self.logger.info(
                        f"          {grade} grade Acc: {correct_[grade]} / {all_[grade]} -> {(correct_[grade]/all_[grade] * 100):.2f} %"
                    )
                file_path = os.path.join(save_path, f"print_{self.angle}.txt")
                file_exists = os.path.exists(file_path)
                with open(file_path, "a") as f:
                    if self.m_dig == "dryness" and not file_exists:
                        f.write(
                            f"Angle, Area, Correlation, P-value, MAE, MAE(==0), MAE(=<1), MAE(=<2)\n"
                        )
                    f.write(
                        f"{self.angle}, {self.m_dig}, {correlation:.2f}, {p_value:.4f}, {mae_:.2f}, {mae_0 * 100:.2f}, {mae_1 * 100:.2f}, {mae_2 * 100:.2f}\n"
                    )

            else:
                if self.wandb_run is not None:
                     self.wandb_run.log({
                        f"Correlation/Total": correlation,
                        f"MAE/Total": mae_,
                        f"MAE_0/Total": mae_0,
                        f"MAE_1/Total": mae_1,
                        f"MAE_2/Total": mae_2
                    })

                file_path = os.path.join(save_path, "print_total.txt")
                file_exists = os.path.exists(file_path)
                with open(file_path, "a") as f:
                    if self.m_dig == "dryness" and not file_exists:
                        f.write(
                            f"Area, Correlation, P-value, MAE, MAE(==0), MAE(=<1), MAE(=<2)\n"
                        )
                    f.write(
                        f"{self.m_dig}, {correlation:.2f}, {p_value:.4f}, {mae_:.2f}, {mae_0 * 100:.2f}, {mae_1 * 100:.2f}, {mae_2 * 100:.2f}\n"
                    )
