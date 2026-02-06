# CNN
## 1. Classification Model Performance (1st_cnn)
| Angle | Area | Correlation | P-value | MAE | MAE(==0) | MAE(=<1) | MAE(=<2) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Total | dryness | 0.03 | 0.1948 | 0.49 | 58.45 | 93.14 | 99.49 |
| Total | forehead_pigmentation | 0.34 | 0.0000 | 0.68 | 49.55 | 88.04 | 95.80 |
| Total | cheek_pigmentation | 0.37 | 0.0000 | 1.06 | 34.22 | 74.60 | 89.65 |
| Total | pore | 0.17 | 0.0000 | 0.66 | 52.01 | 84.27 | 97.99 |
| Total | sagging | 0.31 | 0.0000 | 1.21 | 44.44 | 66.06 | 80.36 |
| Total | forehead_wrinkle | 0.36 | 0.0000 | 1.20 | 38.86 | 68.42 | 84.66 |
| Total | glabellus_wrinkle | 0.45 | 0.0000 | 1.08 | 38.55 | 73.60 | 87.63 |
| Total | perocular_wrinkle | 0.46 | 0.0000 | 1.39 | 35.23 | 64.09 | 79.41 |
| F | dryness | 0.11 | 0.0091 | 0.49 | 57.65 | 93.20 | 99.66 |
| F | forehead_pigmentation | 0.35 | 0.0000 | 0.67 | 49.66 | 88.10 | 96.09 |
| F | cheek_pigmentation | 0.41 | 0.0000 | 1.05 | 33.25 | 75.74 | 90.50 |
| F | pore | 0.20 | 0.0000 | 0.60 | 53.42 | 87.78 | 98.63 |
| F | sagging | 0.33 | 0.0000 | 1.26 | 42.24 | 66.01 | 79.21 |
| F | forehead_wrinkle | 0.35 | 0.0000 | 1.21 | 39.09 | 68.19 | 84.26 |
| F | glabellus_wrinkle | 0.49 | 0.0000 | 1.03 | 39.00 | 75.26 | 89.52 |
| F | perocular_wrinkle | 0.40 | 0.0000 | 1.40 | 37.23 | 63.45 | 78.51 |
| L | dryness | -0.01 | 0.8677 | 0.48 | 58.93 | 93.11 | 99.49 |
| L | forehead_pigmentation | 0.12 | 0.0148 | 0.84 | 40.05 | 85.20 | 93.88 |
| L | cheek_pigmentation | 0.22 | 0.0000 | 1.27 | 28.68 | 66.24 | 84.26 |
| L | pore | 0.03 | 0.5548 | 0.86 | 45.64 | 73.08 | 95.90 |
| L | sagging | 0.26 | 0.0000 | 1.21 | 45.54 | 65.84 | 79.95 |
| L | forehead_wrinkle | 0.11 | 0.0309 | 1.39 | 35.28 | 62.18 | 79.95 |
| L | glabellus_wrinkle | 0.21 | 0.0000 | 1.30 | 32.99 | 66.49 | 83.51 |
| L | perocular_wrinkle | 0.41 | 0.0000 | 1.62 | 30.40 | 56.78 | 72.86 |
| R | dryness | -0.06 | 0.2687 | 0.47 | 59.44 | 93.62 | 99.74 |
| R | forehead_pigmentation | 0.22 | 0.0000 | 0.80 | 43.62 | 83.42 | 94.13 |
| R | cheek_pigmentation | 0.17 | 0.0007 | 1.23 | 31.38 | 68.37 | 84.44 |
| R | pore | 0.16 | 0.0014 | 0.75 | 48.21 | 79.49 | 97.44 |
| R | sagging | 0.17 | 0.0006 | 1.31 | 42.57 | 62.62 | 78.47 |
| R | forehead_wrinkle | 0.13 | 0.0074 | 1.40 | 35.53 | 61.68 | 79.70 |
| R | glabellus_wrinkle | 0.28 | 0.0000 | 1.20 | 35.82 | 70.88 | 84.28 |
| R | perocular_wrinkle | 0.39 | 0.0000 | 1.64 | 29.19 | 57.61 | 73.60 |
| L30 | dryness | -0.05 | 0.5225 | 0.50 | 58.67 | 92.86 | 98.98 |
| L30 | forehead_pigmentation | 0.67 | 0.0000 | 0.39 | 66.33 | 95.41 | 98.98 |
| L30 | cheek_pigmentation | 0.60 | 0.0000 | 0.75 | 43.65 | 85.28 | 97.46 |
| L30 | pore | 0.33 | 0.0000 | 0.54 | 57.95 | 89.74 | 98.46 |
| L30 | sagging | 0.48 | 0.0000 | 1.02 | 49.01 | 69.80 | 85.64 |
| L30 | forehead_wrinkle | 0.70 | 0.0000 | 0.79 | 45.18 | 81.73 | 95.43 |
| L30 | glabellus_wrinkle | 0.75 | 0.0000 | 0.79 | 48.45 | 81.96 | 92.27 |
| L30 | perocular_wrinkle | 0.79 | 0.0000 | 0.89 | 40.20 | 78.89 | 92.96 |
| R30 | dryness | 0.03 | 0.6857 | 0.51 | 57.65 | 92.35 | 98.98 |
| R30 | forehead_pigmentation | 0.67 | 0.0000 | 0.42 | 63.27 | 95.41 | 98.98 |
| R30 | cheek_pigmentation | 0.63 | 0.0000 | 0.68 | 47.45 | 86.22 | 97.96 |
| R30 | pore | 0.31 | 0.0000 | 0.54 | 57.95 | 89.74 | 98.97 |
| R30 | sagging | 0.46 | 0.0000 | 1.05 | 48.02 | 69.80 | 83.17 |
| R30 | forehead_wrinkle | 0.72 | 0.0000 | 0.79 | 45.69 | 81.73 | 94.42 |
| R30 | glabellus_wrinkle | 0.71 | 0.0000 | 0.87 | 43.81 | 79.90 | 92.27 |
| R30 | perocular_wrinkle | 0.80 | 0.0000 | 0.84 | 40.10 | 80.71 | 95.94 |

## 2. Regression Model Performance (1st_cnn)
| Angle | Area | Correlation | P-value | MAE | MAPE | NMAE |
| --- | --- | --- | --- | --- | --- | --- |
| Total | pigmentation | 0.52 | 0.0000 | 46.08 | 0.33 | 0.15 |
| Total | forehead_moisture | 0.09 | 0.0053 | 7.83 | 0.14 | 0.13 |
| Total | cheek_moisture | 0.17 | 0.0000 | 8.34 | 0.14 | 0.16 |
| Total | chin_moisture | 0.27 | 0.0000 | 8.27 | 0.15 | 0.15 |
| Total | forehead_elasticity_R2 | 0.17 | 0.0000 | 0.09 | 0.15 | 0.16 |
| Total | cheek_elasticity_R2 | 0.36 | 0.0000 | 0.07 | 0.14 | 0.12 |
| Total | chin_elasticity_R2 | 0.04 | 0.2882 | 0.09 | 0.18 | 0.17 |
| Total | perocular_wrinkle_Ra | 0.39 | 0.0000 | 4.10 | 0.19 | 0.16 |
| Total | cheek_pore | 0.36 | 0.0000 | 438.53 | 0.87 | 0.31 |
| F | pigmentation | 0.53 | 0.0000 | 45.34 | 0.33 | 0.14 |
| F | forehead_moisture | 0.12 | 0.0384 | 7.69 | 0.13 | 0.15 |
| F | cheek_moisture | 0.13 | 0.0011 | 8.32 | 0.14 | 0.16 |
| F | chin_moisture | 0.27 | 0.0000 | 8.35 | 0.15 | 0.16 |
| F | forehead_elasticity_R2 | 0.15 | 0.0129 | 0.09 | 0.15 | 0.16 |
| F | cheek_elasticity_R2 | 0.37 | 0.0000 | 0.07 | 0.14 | 0.12 |
| F | chin_elasticity_R2 | 0.02 | 0.7428 | 0.09 | 0.18 | 0.20 |
| F | perocular_wrinkle_Ra | 0.39 | 0.0000 | 4.19 | 0.18 | 0.18 |
| F | cheek_pore | 0.38 | 0.0000 | 441.36 | 0.87 | 0.32 |
| L | pigmentation | 0.38 | 0.0000 | 55.55 | 0.37 | 0.20 |
| L | forehead_moisture | 0.05 | 0.4747 | 7.92 | 0.14 | 0.13 |
| L | cheek_moisture | 0.20 | 0.0049 | 8.50 | 0.14 | 0.12 |
| L | chin_moisture | 0.29 | 0.0000 | 8.18 | 0.14 | 0.16 |
| L | forehead_elasticity_R2 | 0.08 | 0.2918 | 0.09 | 0.15 | 0.16 |
| L | cheek_elasticity_R2 | 0.29 | 0.0001 | 0.08 | 0.15 | 0.13 |
| L | chin_elasticity_R2 | 0.05 | 0.5282 | 0.10 | 0.18 | 0.19 |
| L | perocular_wrinkle_Ra | 0.34 | 0.0000 | 4.30 | 0.22 | 0.19 |
| L | cheek_pore | 0.25 | 0.0008 | 476.74 | 0.97 | 0.35 |
| R | pigmentation | 0.37 | 0.0000 | 58.69 | 0.39 | 0.19 |
| R | forehead_moisture | 0.06 | 0.4266 | 8.08 | 0.14 | 0.13 |
| R | cheek_moisture | 0.16 | 0.0291 | 8.92 | 0.15 | 0.19 |
| R | chin_moisture | 0.27 | 0.0001 | 8.26 | 0.15 | 0.15 |
| R | forehead_elasticity_R2 | 0.08 | 0.2390 | 0.09 | 0.15 | 0.18 |
| R | cheek_elasticity_R2 | 0.15 | 0.0400 | 0.08 | 0.14 | 0.15 |
| R | chin_elasticity_R2 | -0.02 | 0.7354 | 0.10 | 0.19 | 0.19 |
| R | perocular_wrinkle_Ra | 0.41 | 0.0000 | 4.54 | 0.22 | 0.28 |
| R | cheek_pore | 0.24 | 0.0006 | 557.13 | 1.22 | 0.44 |
| L30 | pigmentation | 0.84 | 0.0000 | 26.33 | 0.22 | 0.09 |
| L30 | forehead_moisture | 0.20 | 0.0507 | 7.54 | 0.13 | 0.17 |
| L30 | cheek_moisture | 0.33 | 0.0009 | 7.65 | 0.13 | 0.18 |
| L30 | chin_moisture | 0.29 | 0.0039 | 8.29 | 0.15 | 0.18 |
| L30 | forehead_elasticity_R2 | 0.34 | 0.0008 | 0.08 | 0.14 | 0.20 |
| L30 | cheek_elasticity_R2 | 0.57 | 0.0000 | 0.07 | 0.13 | 0.14 |
| L30 | chin_elasticity_R2 | 0.18 | 0.0880 | 0.09 | 0.16 | 0.20 |
| L30 | perocular_wrinkle_Ra | 0.60 | 0.0000 | 3.21 | 0.15 | 0.10 |
| L30 | cheek_pore | 0.67 | 0.0000 | 279.03 | 0.42 | 0.17 |
| R30 | pigmentation | 0.88 | 0.0000 | 23.87 | 0.20 | 0.08 |
| R30 | forehead_moisture | 0.13 | 0.1982 | 7.85 | 0.14 | 0.18 |
| R30 | cheek_moisture | 0.39 | 0.0001 | 7.71 | 0.13 | 0.22 |
| R30 | chin_moisture | 0.34 | 0.0006 | 8.20 | 0.15 | 0.20 |
| R30 | forehead_elasticity_R2 | 0.30 | 0.0030 | 0.08 | 0.14 | 0.15 |
| R30 | cheek_elasticity_R2 | 0.54 | 0.0000 | 0.07 | 0.13 | 0.14 |
| R30 | chin_elasticity_R2 | 0.06 | 0.5586 | 0.09 | 0.16 | 0.23 |
| R30 | perocular_wrinkle_Ra | 0.67 | 0.0000 | 3.20 | 0.14 | 0.18 |
| R30 | cheek_pore | 0.68 | 0.0000 | 262.86 | 0.36 | 0.22 |

# Coat

## 1. Classification Model Performance (1st_coat)
| Angle | Area | Correlation | P-value | MAE | MAE(==0) | MAE(=<1) | MAE(=<2) |
| --- | --- | --- | --- | --- | --- | --- | --- |
| Total | dryness | 0.29 | 0.0000 | 0.57 | 49.72 | 93.54 | 99.43 |
| Total | forehead_pigmentation | 0.62 | 0.0000 | 0.52 | 57.43 | 92.18 | 98.64 |
| Total | cheek_pigmentation | 0.73 | 0.0000 | 0.61 | 50.93 | 89.36 | 98.85 |
| Total | pore | 0.47 | 0.0000 | 0.58 | 53.03 | 89.70 | 98.93 |
| Total | sagging | 0.80 | 0.0000 | 0.55 | 57.59 | 89.27 | 97.96 |
| Total | forehead_wrinkle | 0.75 | 0.0000 | 0.86 | 37.90 | 82.18 | 94.30 |
| Total | glabellus_wrinkle | 0.75 | 0.0000 | 0.87 | 37.92 | 84.08 | 94.27 |
| Total | perocular_wrinkle | 0.77 | 0.0000 | 1.17 | 27.05 | 72.07 | 87.76 |
| F | dryness | 0.27 | 0.0000 | 0.62 | 47.11 | 92.18 | 99.15 |
| F | forehead_pigmentation | 0.61 | 0.0000 | 0.53 | 56.46 | 92.01 | 98.30 |
| F | cheek_pigmentation | 0.73 | 0.0000 | 0.60 | 52.08 | 89.40 | 98.81 |
| F | pore | 0.46 | 0.0000 | 0.59 | 53.25 | 88.63 | 98.72 |
| F | sagging | 0.80 | 0.0000 | 0.57 | 57.10 | 88.12 | 98.18 |
| F | forehead_wrinkle | 0.78 | 0.0000 | 0.81 | 39.42 | 84.43 | 95.43 |
| F | glabellus_wrinkle | 0.80 | 0.0000 | 0.79 | 39.69 | 86.43 | 96.05 |
| F | perocular_wrinkle | 0.75 | 0.0000 | 1.19 | 26.90 | 70.90 | 87.90 |
| L | dryness | 0.28 | 0.0000 | 0.55 | 51.79 | 93.37 | 99.74 |
| L | forehead_pigmentation | 0.56 | 0.0000 | 0.60 | 51.79 | 89.29 | 98.72 |
| L | cheek_pigmentation | 0.73 | 0.0000 | 0.65 | 49.49 | 87.56 | 98.48 |
| L | pore | 0.50 | 0.0000 | 0.57 | 52.31 | 91.79 | 99.23 |
| L | sagging | 0.77 | 0.0000 | 0.58 | 56.19 | 87.87 | 97.77 |
| L | forehead_wrinkle | 0.71 | 0.0000 | 0.98 | 34.52 | 77.41 | 91.12 |
| L | glabellus_wrinkle | 0.73 | 0.0000 | 1.03 | 30.93 | 79.12 | 91.49 |
| L | perocular_wrinkle | 0.80 | 0.0000 | 1.39 | 18.84 | 64.57 | 81.16 |
| R | dryness | 0.29 | 0.0000 | 0.55 | 51.02 | 94.13 | 100.00 |
| R | forehead_pigmentation | 0.55 | 0.0000 | 0.59 | 52.81 | 89.80 | 98.47 |
| R | cheek_pigmentation | 0.72 | 0.0000 | 0.70 | 45.15 | 86.22 | 98.47 |
| R | pore | 0.41 | 0.0000 | 0.61 | 51.79 | 88.46 | 98.46 |
| R | sagging | 0.79 | 0.0000 | 0.57 | 56.68 | 89.85 | 96.53 |
| R | forehead_wrinkle | 0.72 | 0.0000 | 1.07 | 29.44 | 73.60 | 91.12 |
| R | glabellus_wrinkle | 0.67 | 0.0000 | 1.13 | 29.12 | 75.52 | 89.43 |
| R | perocular_wrinkle | 0.79 | 0.0000 | 1.42 | 16.75 | 63.20 | 82.74 |
| L30 | dryness | 0.35 | 0.0000 | 0.52 | 52.55 | 96.43 | 98.98 |
| L30 | forehead_pigmentation | 0.79 | 0.0000 | 0.34 | 68.88 | 97.96 | 99.49 |
| L30 | cheek_pigmentation | 0.79 | 0.0000 | 0.53 | 54.31 | 93.91 | 99.49 |
| L30 | pore | 0.55 | 0.0000 | 0.57 | 51.79 | 91.28 | 99.49 |
| L30 | sagging | 0.84 | 0.0000 | 0.48 | 60.89 | 91.58 | 99.50 |
| L30 | forehead_wrinkle | 0.84 | 0.0000 | 0.62 | 45.69 | 91.88 | 100.00 |
| L30 | glabellus_wrinkle | 0.84 | 0.0000 | 0.59 | 49.48 | 92.78 | 98.97 |
| L30 | perocular_wrinkle | 0.88 | 0.0000 | 0.67 | 41.21 | 92.46 | 98.99 |
| R30 | dryness | 0.35 | 0.0000 | 0.59 | 47.96 | 93.88 | 98.98 |
| R30 | forehead_pigmentation | 0.77 | 0.0000 | 0.34 | 69.39 | 97.45 | 98.98 |
| R30 | cheek_pigmentation | 0.82 | 0.0000 | 0.51 | 55.10 | 94.39 | 100.00 |
| R30 | pore | 0.55 | 0.0000 | 0.50 | 56.92 | 92.82 | 100.00 |
| R30 | sagging | 0.85 | 0.0000 | 0.49 | 60.40 | 92.08 | 99.01 |
| R30 | forehead_wrinkle | 0.83 | 0.0000 | 0.60 | 49.24 | 92.39 | 97.97 |
| R30 | glabellus_wrinkle | 0.88 | 0.0000 | 0.53 | 52.58 | 95.36 | 99.48 |
| R30 | perocular_wrinkle | 0.89 | 0.0000 | 0.59 | 50.76 | 91.37 | 98.98 |

## 2. Regression Model Performance (1st_coat)
| Angle | Area | Correlation | P-value | MAE | MAPE | NMAE |
| --- | --- | --- | --- | --- | --- | --- |
| Total | pigmentation | 0.78 | 0.0000 | 31.89 | 0.22 | 0.10 |
| Total | forehead_moisture | 0.23 | 0.0000 | 10.00 | 0.16 | 0.12 |
| Total | cheek_moisture | 0.37 | 0.0000 | 10.54 | 0.16 | 0.11 |
| Total | chin_moisture | 0.29 | 0.0000 | 10.32 | 0.16 | 0.11 |
| Total | forehead_elasticity_R2 | 0.49 | 0.0000 | 0.08 | 0.14 | 0.20 |
| Total | cheek_elasticity_R2 | 0.60 | 0.0000 | 0.07 | 0.13 | 0.10 |
| Total | chin_elasticity_R2 | 0.26 | 0.0000 | 0.09 | 0.18 | 0.18 |
| Total | perocular_wrinkle_Ra | 0.57 | 0.0000 | 4.29 | 0.18 | 0.11 |
| Total | cheek_pore | 0.76 | 0.0000 | 274.81 | 0.43 | 0.13 |
| F | pigmentation | 0.77 | 0.0000 | 32.22 | 0.22 | 0.10 |
| F | forehead_moisture | 0.22 | 0.0002 | 10.26 | 0.16 | 0.13 |
| F | cheek_moisture | 0.37 | 0.0000 | 10.64 | 0.16 | 0.11 |
| F | chin_moisture | 0.32 | 0.0000 | 9.91 | 0.16 | 0.13 |
| F | forehead_elasticity_R2 | 0.49 | 0.0000 | 0.08 | 0.14 | 0.20 |
| F | cheek_elasticity_R2 | 0.59 | 0.0000 | 0.07 | 0.13 | 0.11 |
| F | chin_elasticity_R2 | 0.23 | 0.0001 | 0.09 | 0.18 | 0.18 |
| F | perocular_wrinkle_Ra | 0.53 | 0.0000 | 4.38 | 0.18 | 0.11 |
| F | cheek_pore | 0.76 | 0.0000 | 278.07 | 0.45 | 0.16 |
| L | pigmentation | 0.82 | 0.0000 | 40.22 | 0.27 | 0.22 |
| L | forehead_moisture | 0.23 | 0.0013 | 10.01 | 0.16 | 0.16 |
| L | cheek_moisture | 0.29 | 0.0000 | 11.10 | 0.17 | 0.11 |
| L | chin_moisture | 0.26 | 0.0003 | 11.70 | 0.18 | 0.15 |
| L | forehead_elasticity_R2 | 0.45 | 0.0000 | 0.08 | 0.15 | 0.23 |
| L | cheek_elasticity_R2 | 0.64 | 0.0000 | 0.07 | 0.15 | 0.11 |
| L | chin_elasticity_R2 | 0.24 | 0.0010 | 0.09 | 0.18 | 0.22 |
| L | perocular_wrinkle_Ra | 0.53 | 0.0000 | 4.91 | 0.21 | 0.10 |
| L | cheek_pore | 0.75 | 0.0000 | 316.10 | 0.48 | 0.18 |
| R | pigmentation | 0.81 | 0.0000 | 40.08 | 0.27 | 0.18 |
| R | forehead_moisture | 0.17 | 0.0149 | 10.55 | 0.17 | 0.12 |
| R | cheek_moisture | 0.28 | 0.0001 | 12.03 | 0.18 | 0.12 |
| R | chin_moisture | 0.25 | 0.0004 | 12.08 | 0.19 | 0.15 |
| R | forehead_elasticity_R2 | 0.45 | 0.0000 | 0.08 | 0.14 | 0.23 |
| R | cheek_elasticity_R2 | 0.58 | 0.0000 | 0.07 | 0.14 | 0.10 |
| R | chin_elasticity_R2 | 0.30 | 0.0000 | 0.09 | 0.18 | 0.20 |
| R | perocular_wrinkle_Ra | 0.63 | 0.0000 | 4.80 | 0.20 | 0.19 |
| R | cheek_pore | 0.77 | 0.0000 | 314.02 | 0.49 | 0.15 |
| L30 | pigmentation | 0.95 | 0.0000 | 15.61 | 0.12 | 0.07 |
| L30 | forehead_moisture | 0.37 | 0.0002 | 8.85 | 0.14 | 0.14 |
| L30 | cheek_moisture | 0.65 | 0.0000 | 8.10 | 0.13 | 0.14 |
| L30 | chin_moisture | 0.52 | 0.0000 | 7.66 | 0.13 | 0.15 |
| L30 | forehead_elasticity_R2 | 0.58 | 0.0000 | 0.07 | 0.13 | 0.18 |
| L30 | cheek_elasticity_R2 | 0.69 | 0.0000 | 0.06 | 0.12 | 0.13 |
| L30 | chin_elasticity_R2 | 0.30 | 0.0033 | 0.09 | 0.17 | 0.21 |
| L30 | perocular_wrinkle_Ra | 0.77 | 0.0000 | 2.86 | 0.12 | 0.09 |
| L30 | cheek_pore | 0.86 | 0.0000 | 186.40 | 0.27 | 0.15 |
| R30 | pigmentation | 0.96 | 0.0000 | 14.15 | 0.11 | 0.08 |
| R30 | forehead_moisture | 0.30 | 0.0028 | 9.27 | 0.15 | 0.15 |
| R30 | cheek_moisture | 0.56 | 0.0000 | 8.29 | 0.13 | 0.16 |
| R30 | chin_moisture | 0.52 | 0.0000 | 7.87 | 0.13 | 0.15 |
| R30 | forehead_elasticity_R2 | 0.60 | 0.0000 | 0.07 | 0.13 | 0.20 |
| R30 | cheek_elasticity_R2 | 0.66 | 0.0000 | 0.06 | 0.11 | 0.15 |
| R30 | chin_elasticity_R2 | 0.30 | 0.0030 | 0.09 | 0.17 | 0.19 |
| R30 | perocular_wrinkle_Ra | 0.80 | 0.0000 | 2.90 | 0.12 | 0.18 |
| R30 | cheek_pore | 0.86 | 0.0000 | 182.91 | 0.25 | 0.11 |