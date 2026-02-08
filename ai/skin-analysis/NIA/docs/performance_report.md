# 모델 경량화 실험 보고서

## 개요

서비스 배포를 위해 PyTorch 모델을 ONNX로 변환하고, 추가 경량화 기법들을 실험했습니다. 결론부터 말하면 **FP32 ONNX가 현 상황에서 최선**입니다.

---

## 1. PyTorch vs ONNX 비교

224×224 이미지 기준 CPU 추론 성능:

| 엔진 | 평균 지연 시간 |
|:---|:---:|
| PyTorch | ~78 ms |
| ONNX Runtime | ~108 ms |

ONNX가 약 30ms 느리지만, 서비스 요구사항(100ms 이내)을 충족합니다.

---

## 2. Docker 이미지 크기

ONNX 도입으로 `torch`, `torchvision`, `timm` 의존성을 제거할 수 있었습니다.

| 빌드 타입 | 의존성 크기 | 전체 이미지 |
|:---|:---:|:---:|
| PyTorch | ~1.5 GB | ~1.7 GB |
| ONNX | ~150 MB | ~300 MB |

약 **1.4GB 절감** (90% 감소)

---

## 3. 추가 경량화 실험

더 줄여보려고 여러 기법을 시도했는데, 전부 실패했습니다.

| 기법 | 결과 | 원인 |
|:---|:---:|:---|
| INT8 양자화 | ❌ | `ShapeInferenceError` - 동적 Attention 구조 문제 |
| FP16 변환 | ❌ | `TypeInferenceError` - Loop 연산 내 타입 불일치 |
| onnx-simplifier | ❌ | 동적 Loop 노드 때문에 그래프 단순화 불가 |
| Transformer Optimizer | ⚠️ | 변환은 됐는데 오히려 느려짐 (108ms → 135ms) |

### 실패 원인

`CoAtNet` 아키텍처가 동적 제어 흐름(Dynamic Control Flow)을 사용해서 표준 ONNX 최적화 도구들과 호환이 안 됩니다. 윈도우 어텐션 구조가 정적 그래프 변환을 방해합니다.

---

## 4. 메모리 스파이크 이슈

서버 운영 중 가끔 메모리가 3GB에서 4.5GB로 튀는 현상이 있었습니다. ONNX Runtime의 Memory Arena가 원인이었습니다.

**해결책**: `service_inference.py`에 아래 설정 추가
```python
self.sess_options.enable_cpu_mem_arena = False
```

---

## 5. 최종 권장 구성

| 항목 | 설정 |
|:---|:---|
| 모델 형식 | ONNX FP32 |
| 추론 엔진 | ONNX Runtime (CPUExecutionProvider) |
| 메모리 설정 | `enable_cpu_mem_arena = False` |
| 스레드 설정 | `ONNX_NUM_THREADS=2` |

---

## 6. 향후 경량화가 필요하다면

현재 아키텍처로는 표준 최적화가 안 되므로, 다음 방법을 고려해야 합니다:

- **QAT(Quantization Aware Training)**: 학습 단계부터 양자화 적용
- **아키텍처 변경**: MobileNet, EfficientNet 등 경량 모델로 교체

지금은 FP32로도 90% 경량화를 달성했으므로, 추가 최적화는 필요 시 진행하면 됩니다.
