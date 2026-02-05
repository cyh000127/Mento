import base64
import re
import json
import uuid
import time
import os
import httpx  # 비동기 통신을 위해 httpx 사용
import asyncio
from fastapi.middleware.cors import CORSMiddleware
from fastapi import Header
from fastapi import FastAPI, HTTPException
from dotenv import load_dotenv
from pydantic import BaseModel
from typing import List, Optional
from uvicorn.middleware.proxy_headers import ProxyHeadersMiddleware
from datetime import datetime


# 1. 환경 변수 로드
load_dotenv()

app = FastAPI(title="Cosmetic OCR Scanner API")
app.add_middleware(ProxyHeadersMiddleware, trusted_hosts=["*"])

# 환경 변수 및 설정 값
NAVER_OCR_URL = os.getenv("NAVER_OCR_URL")
NAVER_SECRET_KEY = os.getenv("NAVER_SECRET_KEY")
ES_URL = os.getenv("ES_URL", "http://elasticsearch:9200")

# [전략 2: 한/영 매칭 사전] - 요청하신 대로 생략
MAPPING_DICT = {
    "비레디": "B.READY",
    "올인원": "All-in-one",
    "오브제": "OBgE",
    "쿠션": "Cushion",
    "보습": "Hydration",
    "매트": "Matte",
    "택1": "Pick 1",
    "레티놀": "Retinol",
    "아이디얼포맨": "Ideal for Men",
    "지복합성": "Combination Skin",
    "포맨트": "Forment",
    "시카": "Cica",
    "쉐딩": "Shading",
    "매치업": "Match Up",
    "어워즈": "Awards",
    "바우로": "Bauro",
    "립밤": "Lip Balm",
    "팩": "Pack",
    "컨실러": "Concealer",
    "원오브뎀": "One of them",
    "기획": "Special Set",
    "선크림": "Sun Cream",
    "단품": "Single Item",
    "라운드랩": "ROUND LAB",
    "제이숲": "Jsoop",
    "토너": "Toner",
    "바하": "BHA",
    "에멀젼": "Emulsion",
    "오일": "Oil",
    "100매": "100Sheets",
    "1기획": "1Special Set",
    "크림": "Cream",
    "스웨거": "Swagger",
    "중건성": "Dry/Normal Skin",
    "수분": "Moisture",
    "미닉": "MINIC",
    "브리티시엠": "British M",
    "리우젤": "Reuzel",
    "이아소": "IASO",
    "다운펌": "Down Perm",
    "파우더": "Powder",
    "젤": "Gel",
    "마몽드": "Mamonde",
    "닥터지": "Dr.G",
    "세븐피엠": "SEVENPM",
    "무칸": "MUKAN",
    "다슈": "Dashu",
    "아크네스": "Acnes",
    "라네즈": "Laneige",
    "80매": "80Sheets",
    "앰플": "Ampoule",
    "컬크림": "Curl Cream",
    "그라펜": "Grafen",
    "모이": "Moi",
    "아이브로우": "Eyebrow",
    "리필": "Refill",
    "낫포유": "NOT4U",
    "갸스비": "Gatsby",
    "선스틱": "Sun Stick",
    "파운데이션": "Foundation",
    "비오템": "Biotherm",
    "3종": "3Types",
    "옴므": "Homme",
    "23호": "23No.",
    "뉴트로지나": "Neutrogena",
    "포뷰트": "FOR:BEAUT",
    "톤업": "Tone Up",
    "라끌랑": "Laqlanc",
    "4종": "4Types",
    "헤어": "Hair",
    "우르오스": "ULOS",
    "클렌징": "Cleansing",
    "세럼": "Serum",
    "진정": "Soothing",
    "세트": "Set",
    "정샘물": "JUNG SAEM MOOL",
    "이니스프리": "Innisfree",
    "로션": "Lotion",
    "아이오페": "IOPE",
    "더페이스샵": "The Face Shop",
    "엠도씨": "MdoC",
    "트리트먼트": "Treatment",
    "2입": "2Pcs",
    "헤라": "Hera",
    "증정": "Gift",
    "2종": "2Types",
    "피지오겔": "Physiogel",
    "두잉왓": "Doing What",
    "25호": "25No.",
    "세라마이드": "Ceramide",
    "미프": "MIP",
    "플리프": "FLEEF",
    "히알루론산": "Hyaluronic Acid",
    "헤레카": "Hereka",
    "탄력": "Elasticity",
    "토리든": "Torriden",
    "블랙몬스터": "Black Monster",
    "커리쉴": "CURLYSHYLL",
    "알로에": "Aloe",
    "듀이셀": "DEWYCEL",
    "비비": "BB",
    "박준뷰티랩": "PARKJUNBEAUTYLAB",
    "폴미첼": "Paul Mitchell",
    "마스카라": "Mascara",
    "폼": "Foam",
    "맨즈": "Men's",
    "어드밴스드": "Advanced",
    "라이트": "Light",
    "클래식": "Classic",
    "다크": "Dark",
    "레드": "Red",
    "딥": "Deep",
    "데일리": "Daily",
    "토닉": "Tonic",
    "샌드": "Sand",
    "블랙": "Black",
    "소프트": "Soft",
    "스틱": "Stick",
    "웨이브": "Wave",
    "그루밍": "Grooming",
    "아쿠아": "Aqua",
    "그레이": "Gray",
    "키트": "Kit",
    "퍼펙트": "Perfect",
    "모이스처": "Moisture",
    "프레쉬": "Fresh",
    "베스트": "Best",
    "내추럴": "Natural",
    "포마드": "Pomade",
    "홀드": "Hold",
    "오리지널": "Original",
    "프리미엄": "Premium",
    "리뉴얼": "Renewal",
    "샤인": "Shine",
    "에디션": "Edition",
    "블루": "Blue",
    "포": "For",
    "볼륨": "Volume",
    "익스트림": "Extreme",
    "하드": "Hard",
    "왁스": "Wax",
    "브라운": "Brown",
    "맨": "Men",
    "스프레이": "Spray"
}

# 요청 데이터 모델
class OCRRequest(BaseModel):
    imageUrl: str  # FE에서 보낸 Base64 데이터


# 기존 MatchedProduct는 유지하되 items 대신 matchedProducts로 쓰기 위해 리스트 구성용으로 사용
class MatchedProduct(BaseModel):
    productId: int
    name: str
    brandName: str
    categoryMedium: str
    categorySmall: str
    price: int
    imageUrl: str
    matchScore: float

# data 필드 내부에 들어갈 실제 OCR 결과 데이터
class OCRData(BaseModel):
    recognized: bool
    confidence: float
    matchedProducts: List[MatchedProduct]

# FE가 최종적으로 수신할 공통 래퍼(Wrapper) 모델
class FinalOCRResponse(BaseModel):
    status: str
    ocr_text: Optional[str] = None
    items: List[MatchedProduct] = []
    message: str


# JWT 토큰에서 유저 ID를 추출하는 헬퍼 함수
def get_user_id_from_token(auth_header: str) -> Optional[str]:
    try:
        # 1. "Bearer <token>" 분리
        token = auth_header.split(" ")[1]

        # 2. JWT의 페이로드(두 번째 부분) 추출
        payload_part = token.split(".")[1]

        # 3. Base64 디코딩 (패딩 오류 방지)
        # base64.urlsafe_b64decode를 사용하면 더 안전합니다.
        decoded_payload = base64.urlsafe_b64decode(payload_part + "==").decode("utf-8")

        # 4. JSON 파싱
        payload_data = json.loads(decoded_payload)

        # 팀원분 코드에서 .subject()에 넣었으므로 'sub' 필드 확인
        # 만약 KEY_ID가 'id'라면 payload_data.get("id")도 가능합니다.
        user_id = payload_data.get("sub")
        return user_id
    except Exception as e:
        print(f"⚠️ 토큰 파싱 에러: {e}")
        return None


# ==========================================
# SECTION 2: 고도화된 전처리 (Slicing & Mapping)
# ==========================================
def preprocess_ocr_text(full_text: str):
    """
    OCR로 추출된 텍스트에서 노이즈를 제거하고 검색 키워드를 확장합니다.
    """
    # 1. 노이즈 제거를 위해 특수문자만 살짝 정리한 텍스트
    clean_text = re.sub(r'[^가-힣a-zA-Z0-9\s]', ' ', full_text)

    # 2. [전략 1: 노이즈 키워드 기반 절단]
    noise_keywords = ["사용방법", "주의사항", "전성분", "제조번호", "책임판매", "제조원", "MADE IN"]
    for keyword in noise_keywords:
        if keyword in clean_text:
            clean_text = clean_text.split(keyword)[0]

    # 3. [핵심 수정] 한/영 동의어 확장 (포함 여부로 체크)
    search_terms = []

    # 공백과 점을 완전히 제거한 '비교용 텍스트' 생성 (예: "B. READY" -> "BREADY")
    match_target = full_text.replace(" ", "").replace(".", "").upper()

    for kor_key, eng_val in MAPPING_DICT.items():
        # 매핑 사전의 키(예: "비레디")가 인식된 전체 문장에 포함되어 있는지 확인
        if kor_key in match_target or eng_val.replace(" ", "").upper() in match_target:
            search_terms.append(kor_key)
            search_terms.append(eng_val)

    # 4. 기존 방식대로 상위 단어들도 추가 (일반 검색용)
    words = clean_text.split()
    search_terms.extend(words[:20])

    # 중복 제거 후 쿼리 스트링 반환
    result = " ".join(list(dict.fromkeys(search_terms)))

    # 디버깅을 위해 로그 출력 (컨테이너 로그에서 확인 가능)
    print(f"🔍 [OCR 원본]: {full_text[:50]}")
    print(f"🔍 [확장된 검색어]: {result}")

    return result

# ==========================================
# SECTION 3: Elasticsearch 검색 (비동기 POST 방식)
# ==========================================
async def search_products_in_es(ocr_text: str, limit: int = 5):
    """
    정제된 텍스트를 바탕으로 ES 'products' 인덱스에서 비동기로 검색을 수행합니다.
    """
    refined_query = preprocess_ocr_text(ocr_text)
    if not refined_query:
        return []

    # 1. 쿼리 필드명을 새로운 인덱스 구조(name, brandName)에 맞게 수정
    search_query = {
        "size": limit,
        "query": {
            "bool": {
                "should": [
                    { "match": { "name": { "query": refined_query, "boost": 5 } } },
                    { "match": { "brandName": { "query": refined_query, "boost": 2 } } },
                    { "match": { "name": { "query": refined_query, "fuzziness": "AUTO" } } }
                ]
            }
        }
    }

    try:
        target_url = f"{ES_URL}/products/_search"
        async with httpx.AsyncClient() as client:
            response = await client.post(target_url, json=search_query, timeout=5.0)

        if response.status_code == 200:
            hits_data = response.json().get('hits', {}).get('hits', [])
            matched_products = []

            # 2. 검색 결과를 FE가 원하는 형식으로 매핑
            for hit in hits_data:
                source = hit['_source']
                matched_products.append({
                    "productId": source.get("productId"),
                    "name": source.get("name"),
                    "brandName": source.get("brandName"),
                    "categoryMedium": source.get("categoryMedium"),
                    "categorySmall": source.get("categorySmall"),
                    "price": source.get("price"),
                    "imageUrl": source.get("imageUrl"),
                    "matchScore": round(hit.get("_score", 0), 2)  # ES 점수를 matchScore로 추가
                })
            return matched_products

        print(f"⚠️ ES 응답 오류: {response.status_code}")
        return []
    except Exception as e:
        print(f"❌ ES 검색 중 오류: {e}")
        return []

# ==========================================
# SECTION 4: 메인 비즈니스 로직 (OCR 스캔)
# ==========================================
@app.post("/api/ocr/products/recognize", response_model=FinalOCRResponse)
async def scan_cosmetic(
        request: OCRRequest,
        authorization: Optional[str] = Header(None) # 이 부분을 추가해야 헤더에서 토큰을 읽어옵니다.
):
    current_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

    # 1. 헤더 체크
    if not authorization or not authorization.startswith("Bearer "):
        return FinalOCRResponse(status="fail", items=[], message="인증 헤더가 누락되었습니다.")

    # 2. 토큰에서 user_id 추출
    user_id = get_user_id_from_token(authorization)
    if not user_id:
        return FinalOCRResponse(status="fail", items=[], message="유효하지 않은 토큰입니다.")

    # 3. Spring Boot에 유저 확인 요청 (프록시 승인)
    try:
        # Docker 네트워크 내부 주소: http://backend:8080
        target_url = f"http://backend:8080/api/v1/users/{user_id}"

        async with httpx.AsyncClient() as client:
            # 유저의 토큰을 그대로 전달하여 Spring Boot가 검증하게 함
            auth_res = await client.get(target_url, headers={"Authorization": authorization}, timeout=3.0)

        if auth_res.status_code != 200:
            return FinalOCRResponse(status="fail", items=[], message="인증 서버로부터 승인을 받지 못했습니다.")

    except Exception as e:
        print(f"❌ BE 통신 에러: {e}")
        return FinalOCRResponse(status="error", items=[], message="인증 서버와 통신할 수 없습니다.")

    try:
        # 1. Base64 데이터 디코딩 (기존 로직 동일)
        image_raw = request.imageUrl
        if "," in image_raw:
            header, image_raw = image_raw.split(",", 1)
            file_ext = header.split(';')[0].split('/')[-1]
            if file_ext == 'jpeg': file_ext = 'jpg'
        else:
            file_ext = 'jpg'

        content = base64.b64decode(image_raw)

        # 2. Naver Clova OCR 요청 구성 (기존 로직 동일)
        request_json = {
            'images': [{'format': file_ext, 'name': 'product_image'}],
            'requestId': str(uuid.uuid4()),
            'version': 'V2',
            'timestamp': int(round(time.time() * 1000))
        }

        # 3. OCR 호출 (기존 로직 동일)
        async with httpx.AsyncClient() as client:
            headers = {'X-OCR-SECRET': NAVER_SECRET_KEY}
            payload = {'message': json.dumps(request_json)}
            files = {'file': (f"image.{file_ext}", content)}
            ocr_response = await client.post(NAVER_OCR_URL, headers=headers, data=payload, files=files, timeout=15.0)

        if ocr_response.status_code != 200:
            return FinalOCRResponse(status="error", items=[], message="OCR 서비스 응답 오류")

        # 4. 결과 분석 및 반환 구조 변경
        res_data = ocr_response.json()

        if 'images' in res_data and res_data['images'][0]['inferResult'] == 'SUCCESS':
            image_res = res_data['images'][0]
            full_text = " ".join([field['inferText'] for field in image_res['fields']])

            # 검색 로직 호출 (이미 리스트를 반환함)
            candidate_products = await search_products_in_es(full_text, limit=5)

            if candidate_products:
                return FinalOCRResponse(
                    status="success",
                    ocr_text=full_text,
                    items=candidate_products, # List[MatchedProduct] 형태
                    message="유사한 상품 후보를 찾았습니다."
                )
            else:
                return FinalOCRResponse(
                    status="partial_success",
                    ocr_text=full_text,
                    items=[],
                    message="텍스트는 인식했으나 일치하는 상품 후보가 없습니다."
                )
        else:
            return FinalOCRResponse(
                status="fail",
                items=[],
                message="이미지 인식에 실패했습니다."
            )

    except Exception as e:
        print(f"❌ 오류 발생: {e}")
        return FinalOCRResponse(
            status="error",
            items=[],
            message=f"서버 내부 오류: {str(e)}"
        )

if __name__ == "__main__":
    import uvicorn
    # 포트 1000번에서 실행
    uvicorn.run(app, host="0.0.0.0", port=1000)