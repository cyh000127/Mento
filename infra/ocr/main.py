from fastapi import FastAPI, File, UploadFile, HTTPException
import requests
import uuid
import time
import json
import os
from dotenv import load_dotenv

# .env 파일에서 환경 변수 로드
load_dotenv()

app = FastAPI()

# --- 환경 변수 설정 ---
NAVER_OCR_URL = os.getenv("NAVER_OCR_URL")
NAVER_SECRET_KEY = os.getenv("NAVER_SECRET_KEY")
# Docker app-network 내부망 주소 (기본값 설정)
ES_URL = os.getenv("ES_URL", "http://elasticsearch:9200")

def search_products_in_es(ocr_text: str, limit: int = 5):
    """
    Elasticsearch에서 Fuzzy Query를 사용하여 연관 상품 리스트를 가져옵니다.
    """
    query_text = ocr_text.strip()
    if not query_text:
        return []

    # Elasticsearch 검색 쿼리: 오타 허용(Fuzzy) 및 가중치 부여
    search_query = {
        "size": limit,  # 유저에게 보여줄 후보 개수
        "query": {
            "multi_match": {
                "query": query_text,
                "fields": ["name^3", "brand_name", "volume"],  # 상품명에 가중치 3배
                "fuzziness": "AUTO",  # 오타 자동 보정
                "operator": "or"
            }
        }
    }

    try:
        # 인덱스명은 Kibana에서 CSV 업로드 시 설정한 이름을 사용해야 합니다 (여기서는 'products')
        response = requests.get(f"{ES_URL}/products/_search", json=search_query)

        if response.status_code == 200:
            hits = response.json().get('hits', {}).get('hits', [])
            # _source 데이터만 추출하여 리스트화
            return [hit['_source'] for hit in hits]
        return []
    except Exception as e:
        print(f"❌ ES 검색 중 오류 발생: {e}")
        return []

@app.post("/api/ocr/scan-cosmetic")
async def scan_cosmetic(file: UploadFile = File(...)):
    """
    클라이언트로부터 이미지를 받아 OCR 텍스트 추출 후 ES 검색 결과를 반환합니다.
    """
    # 1. 파일 데이터 읽기
    content = await file.read()
    file_ext = file.filename.split('.')[-1]

    # 2. Clova OCR 요청 구성
    request_json = {
        'images': [{'format': file_ext, 'name': 'product_image'}],
        'requestId': str(uuid.uuid4()),
        'version': 'V2',
        'timestamp': int(round(time.time() * 1000))
    }

    payload = {'message': json.dumps(request_json)}
    files = [('file', (file.filename, content))]
    headers = {'X-OCR-SECRET': NAVER_SECRET_KEY}

    try:
        # 3. Naver Clova OCR 호출
        ocr_response = requests.post(NAVER_OCR_URL, headers=headers, data=payload, files=files)

        if ocr_response.status_code != 200:
            raise HTTPException(status_code=500, detail="OCR 서비스 응답 오류")

        res_data = ocr_response.json()

        # 4. 결과 분석 및 Elasticsearch 검색
        if 'images' in res_data and res_data['images'][0]['inferResult'] == 'SUCCESS':
            fields = res_data['images'][0]['fields']
            full_text = " ".join([field['inferText'] for field in fields])

            # --- Elasticsearch 다중 검색 실행 ---
            candidate_products = search_products_in_es(full_text, limit=5)
            # ----------------------------------

            if candidate_products:
                return {
                    "status": "success",
                    "ocr_text": full_text,
                    "items": candidate_products,
                    "count": len(candidate_products),
                    "message": "유사한 상품 후보를 찾았습니다."
                }
            else:
                return {
                    "status": "partial_success",
                    "ocr_text": full_text,
                    "items": [],
                    "message": "텍스트는 인식했으나 일치하는 상품 후보가 없습니다."
                }
        else:
            return {"status": "fail", "message": "이미지 인식에 실패했습니다."}

    except Exception as e:
        print(f"❌ 서버 로직 오류: {e}")
        raise HTTPException(status_code=500, detail=str(e))

if __name__ == "__main__":
    import uvicorn
    # Nginx 및 Docker 설정에 맞춘 내부 포트 1000번 실행
    uvicorn.run(app, host="0.0.0.0", port=1000)