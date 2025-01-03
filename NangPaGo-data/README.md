# nangpago-data

## 데이터 전처리 및 CSV 업로드 스크립트

`nangpago-data` 폴더는 **CSV 데이터를 MySQL 및 ElasticSearch**로 업로드하는 작업을 자동화하는 스크립트 모음으로, 데이터 전처리부터 업로드까지의 과정이 포함되어 있으며, 데이터를 효율적으로 처리하고 다양한 저장소로 전송할 수 있도록 합니다.

## 프로젝트 구성

- **데이터 전처리**: CSV 데이터를 MySQL 및 ElasticSearch에 적합한 형식으로 변환.
- **CSV 업로드 스크립트**: 전처리된 데이터를 MySQL과 ElasticSearch에 업로드하는 자동화된 스크립트.
- **폴더 구조**:
  - `datasets/`: 데이터 파일 (CSV 파일 포함).
  - `create_recipe_cleaned_csv.ipynb`: 데이터 전처리 관련 스크립트.
  - `mysql/`: MySQL에 데이터를 업로드하는 스크립트 (파이썬).
  - `elasticsearch/`: ElasticSearch에 데이터를 업로드하는 스크립트.
  - `data/`: 

### 1. 원본 데이터 전처리
    `create_recipe_cleaned_csv.ipynb` `Run All` 실행 -> `datasets/recipe_cleaned.csv` 파일 생성.


### 2. ElasticSearch 데이터 업로드

1) 기존의 데이터를 Bulk 형식으로 변환
    ```bash
    python3 elastic/ingredients_dictionary/create_bulk_type.py
    ```

    - 기존 데이터의 형식 ```{ id, name }```

    - bulk 형식으로 변환 결과
        ```
        { "index":{ "_index" : "ingredients_dictionary", "_type" : "_doc" } }
        {"name": name }
        ```

2) 환경변수 세팅
    ```bash
    export ELASTICSEARCH_HOST={ELASTICSEARCH_IP:ELASTICSEARCH_PORT} # MAC
    set ELASTICSEARCH_HOST={ELASTICSEARCH_IP:ELASTICSEARCH_PORT} # Window
    ```
    - 파일 구조
        ```bash
        .
        └── ingredients_dictionary
            ├── create_bulk_type.py
            ├── ingredients_dictionary.json
            └── init_csv2es_ingredients_dictionary.sh
        ```
3) 명령어 실행
이 스크립트는 전처리 후 bulk 타입으로 변형된 데이터를 
    ```bash
    chmod +x init_csv2es_ingredients_dictionary.sh
    ./init_csv2es_ingredients_dictionary.sh
    ```
    ElasticSearch에 삽입합니다.

### 2. MySQL 데이터 업로드
이 스크립트는 전처리된 데이터를 MySQL에 삽입합니다.
```bash

python3 init_csv2sql_ingredients_dictionary --host {DB_IP} --user {DB_USERNAME} --password {DB_PWD} --database {DB_NAME} --port {DB_PORT}
```
