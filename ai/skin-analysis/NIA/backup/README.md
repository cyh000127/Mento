# Skin Analysis AI Server

This project is divided into two main sections:

## `app/` (User Code)
Contains the FastAPI server and analysis logic developed by the current maintainer.
- `server.py`: Main entry point for the FastAPI server.
- `analysis/`: Contains inference logic (`inference.py`) and scoring logic (`scoring.py`).
- `resources/`: Configuration files and resources.

## `core/` (Legacy Code)
Contains the original codebase for model training and evaluation.
- Licensed under MIT (see `core/LICENSE.txt`).
- Includes model definitions (`model.py`), data loading (`data_loader.py`), and utility scripts.

## Installation & Usage
1. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```
2. Run the AI Server:
   ```bash
   uv run python -m app.server
   ```
