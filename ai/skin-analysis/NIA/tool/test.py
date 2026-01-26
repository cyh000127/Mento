import shutil
import sys
import os
import argparse
import yaml

os.environ["CUDA_DEVICE_ORDER"] = "PCI_BUS_ID"
# os.environ["CUDA_VISIBLE_DEVICES"] = "5"


workspace_path = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
sys.path.insert(0, workspace_path)
os.chdir(workspace_path)

sys.stderr = open(sys.stderr.fileno(), mode="w", buffering=1)

from torch.utils.data import DataLoader
from tool.utils import resume_checkpoint, fix_seed
from tool.data_loader import CustomDataset
from tool.logger import setup_logger
from tool.model import Model_test
from tool.model import Model_test
from torchvision import models
import wandb
import timm

def parse_args():
    parser = argparse.ArgumentParser()

    parser.add_argument(
        "--name",
        default="none",
        type=str,
    )

    parser.add_argument("--equ", type=int, nargs="+", default=[1, 2, 3], choices=[1, 2, 3])

    parser.add_argument(
        "--mode",
        default="class",
        choices=["regression", "class"],
        type=str,
    )

    parser.add_argument(
        "--check_root",
        default="",
        type=str,
    )

    parser.add_argument(
        "--batch_size",
        default=32,
        type=int,
    )

    parser.add_argument(
        "--seed",
        default=1,
        type=int,
    )

    parser.add_argument(
        "--res",
        # default=256, # resnet
        default=224,
        type=int,
    )

    parser.add_argument(
        "--num_workers",
        default=8,
        type=int,
    )

    parser.add_argument("--wandb", action="store_true", help="Use Wandb")
    parser.add_argument("--wandb_project", type=str, default="mento", help="Wandb Project Name")
    parser.add_argument("--wandb_entity", type=str, default="kangseunghun9927-sw-", help="Wandb Entity")

    args = parser.parse_args()

    return args


def main(args):
    seed = args.name.split("st")[0]
    if seed.isdigit():
        ValueError, f"It's not correct name, {args.name} -> {seed}"
        
    args.seed = int(seed)
    fix_seed(int(seed))

    check_path = os.path.join(
        args.check_root, "checkpoint", args.mode, args.name
    )

    if os.path.isdir(os.path.join(check_path, "log", "eval")):
        shutil.rmtree(os.path.join(check_path, "log", "eval"))

    args.log_path = os.path.join(check_path, "log")

    logger = setup_logger(args.name, os.path.join(args.log_path, "eval"))
    logger.info("Command Line: " + " ".join(sys.argv))

    yaml_file_path = os.path.join(check_path, "code", "test_config.yaml")
    os.makedirs(os.path.dirname(yaml_file_path), exist_ok=True)
    with open(yaml_file_path, "w") as yaml_file:
        yaml.dump(vars(args), yaml_file, default_flow_style=False)

    model_num_class = (
        {"dryness": 5, "pigmentation": 6, "pore": 6, "sagging": 6, "wrinkle": 7}
        if args.mode == "class"
        else {
            "pigmentation": 1,
            "moisture": 1,
            "elasticity_R2": 1,
            "wrinkle_Ra": 1,
            "pore": 1,
        }
    )
    
    model_list = {
        # key: models.resnet50(num_classes=value)
        key: timm.create_model("coatnet_2_rw_224", pretrained=True, num_classes=value)
        for key, value in model_num_class.items()
    }

    model_path = os.path.join(check_path, "save_model")
    save_log_path = os.path.join(check_path, "log", "save-log")

    if os.path.isdir(save_log_path):
        shutil.rmtree(save_log_path)

    if os.path.isdir(model_path):
        for path in os.listdir(model_path):
            dig_path = os.path.join(model_path, path)
            if os.path.isfile(os.path.join(dig_path, "state_dict.bin")):
                print(f"\033[92mResuming......{dig_path}\033[0m")
                model_list[path], _, _, _ = resume_checkpoint(
                    args,
                    model_list[path],
                    os.path.join(dig_path, "state_dict.bin"),
                    path,
                    False,
                )
    else:
        shutil.rmtree(check_path)
        assert 0, "Incorrect checkpoint path"

    dataset = (
        CustomDataset(args, logger, "test", special=True)
        if args.mode == "class"
        else CustomDataset(args, logger, "test", special=True)
    )
    resnet_model = Model_test(args, logger)

    model_area_dict = (
        {
            "dryness": ["dryness"],
            "pigmentation": ["forehead_pigmentation", "cheek_pigmentation"],
            "pore": ["pore"],
            "sagging": ["sagging"],
            "wrinkle": ["forehead_wrinkle", "glabellus_wrinkle", "perocular_wrinkle"],
        }
        if args.mode == "class"
        else {
            "pigmentation": ["pigmentation"],
            "moisture": ["forehead_moisture", "cheek_moisture", "chin_moisture"],
            "elasticity_R2": [
                "forehead_elasticity_R2",
                "cheek_elasticity_R2",
                "chin_elasticity_R2",
            ],
            "wrinkle_Ra": ["perocular_wrinkle_Ra"],
            "pore": ["cheek_pore"],
        }
    )

    for key in model_list:
        if args.wandb:
            wandb_run = wandb.init(
                project=args.wandb_project,
                entity=args.wandb_entity,
                name=f"{args.name}_test_{key}",
                config=vars(args),
                reinit=True
            )
            resnet_model.wandb_run = wandb_run
        else:
            resnet_model.wandb_run = None

        model = model_list[key].cuda()
        for w_key in model_area_dict[key]:
            testset, _ = dataset.load_dataset(w_key)
            testset_loader = DataLoader(
                dataset=testset,
                batch_size=args.batch_size,
                num_workers=args.num_workers,
                shuffle=False,
            )
            resnet_model.test(model, testset_loader, w_key)
            resnet_model.print_test()
    resnet_model.save_value()


if __name__ == "__main__":
    args = parse_args()
    main(args)
