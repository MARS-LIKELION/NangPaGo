#!/bin/bash

# 환경변수로 설정된 Elasticsearch URI 확인
if [ -z "$ELASTICSEARCH_URI" ]; then
  echo "Error: ELASTICSEARCH_URI 환경변수가 설정되지 않았습니다."
  exit 1
fi

echo "Elasticsearch URI is set to: $ELASTICSEARCH_URI"

# 1. 기존 인덱스 삭제
curl -XDELETE "${ELASTICSEARCH_URI}/ingredients_dictionary?pretty" -H 'Content-Type: application/json'

# 2. 인덱스 생성 및 설정
curl -XPUT "${ELASTICSEARCH_URI}/ingredients_dictionary?include_type_name=true&pretty" -H 'Content-Type: application/json' -d '{
  "settings": {
    "index": {
      "number_of_replicas": "0",
      "max_ngram_diff": 50,
      "analysis": {
        "filter": {
          "suggest_filter": {
            "type": "ngram",
            "min_gram": 1,
            "max_gram": 50
          }
        },
        "analyzer": {
          "my_ngram_analyzer": {
            "tokenizer": "my_ngram_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "suggest_search_analyzer": {
            "type": "custom",
            "tokenizer": "jaso_search_tokenizer",
            "filter": [
              "lowercase"
            ]
          },
          "suggest_index_analyzer": {
            "type": "custom",
            "tokenizer": "jaso_index_tokenizer",
            "filter": [
              "lowercase",
              "suggest_filter"
            ]
          }
        },
        "tokenizer": {
          "jaso_search_tokenizer": {
            "type": "jaso_tokenizer",
            "mistype": true,
            "chosung": false
          },
          "jaso_index_tokenizer": {
            "type": "jaso_tokenizer",
            "mistype": true,
            "chosung": true
          },
          "my_ngram_tokenizer": {
            "type": "ngram",
            "min_gram": "1",
            "max_gram": "10"
          }
        }
      }
    }
  },
  "mappings": {
    "_doc": {
      "properties": {
        "ingredient_id": {
          "type": "long"
        },
        "name": {
          "type": "text",
          "fields": {
            "ngram": {
              "type": "text",
              "analyzer": "my_ngram_analyzer"
            },
            "jaso": {
              "type": "text",
              "analyzer": "suggest_index_analyzer"
            }
          }
        }
      }
    }
  }
}'
