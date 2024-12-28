package com.mars.NangPaGo.domain.recipe.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import com.mars.NangPaGo.domain.recipe.dto.RecipeEsDto;
import com.mars.NangPaGo.domain.recipe.entity.RecipeES;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeEsService {

    private final ElasticsearchClient elasticsearchClient;

    // Elasticsearch 인덱스 생성 메서드
    public String createIndex() {
        try {
            // JSON 형식의 인덱스 설정
            String indexSettings = """
        {
          "settings": {
            "analysis": {
              "analyzer": {
                "nori_analyzer": {
                  "type": "custom",
                  "tokenizer": "nori_tokenizer",
                  "filter": ["lowercase", "nori_readingform"]
                }
              }
            }
          },
          "mappings": {
            "properties": {
              "id": { "type": "keyword" },
              "name": { "type": "text", "analyzer": "nori_analyzer" },
              "ingredients": {
                "type": "text",
                "analyzer": "nori_analyzer",
                "fields": {
                  "keyword": { "type": "keyword" }
                }
              },
              "ingredientsTag": {
                "type": "text",
                "analyzer": "nori_analyzer",
                "fields": {
                  "keyword": { "type": "keyword" }
                }
              }
            }
          }
        }
        """;

            // Elasticsearch 인덱스 생성 요청
            CreateIndexRequest request = new CreateIndexRequest.Builder()
                    .index("recipes")
                    .withJson(new StringReader(indexSettings))
                    .build();

            elasticsearchClient.indices().create(request);
            return "Elasticsearch 인덱스가 성공적으로 생성되었습니다!";
        } catch (Exception e) {
            log.error("Elasticsearch 인덱스 생성 중 오류 발생: {}", e.getMessage());
            return "Elasticsearch 인덱스 생성 실패: " + e.getMessage();
        }
    }




    // CSV 데이터 삽입 메서드
    public String insertRecipesFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                    .withFirstRecordAsHeader()
                    .withIgnoreSurroundingSpaces()
                    .withTrim());

            List<RecipeES> recipeESList = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                // CSV 데이터 읽기 및 필드 처리
                String id = record.get("RCP_SEQ").trim();
                String name = record.get("RCP_NM").trim();
                String recipeImageUrl = record.get("ATT_FILE_NO_MAIN").trim();
                List<String> ingredientsList = List.of(record.get("RCP_PARTS_DTLS").split(",")).stream()
                        .map(String::trim)
                        .filter(ingredient -> !ingredient.isEmpty())
                        .toList();
                List<String> ingredientsTag = ingredientsList.stream().limit(5).toList();

                // 생성자를 사용하여 RecipeES 객체 생성
                RecipeES recipeES = new RecipeES(id, name, recipeImageUrl, ingredientsList, ingredientsTag);
                recipeESList.add(recipeES);
            }

            // 데이터 저장
            for (RecipeES recipe : recipeESList) {
                elasticsearchClient.index(i -> i
                        .index("recipes")
                        .id(recipe.getId())
                        .document(recipe)
                );
            }

            return "데이터가 성공적으로 저장되었습니다!";
        } catch (Exception e) {
            log.error("CSV 데이터 삽입 중 오류 발생: {}", e.getMessage());
            return "데이터 삽입 실패: " + e.getMessage();
        }
    }


    // 검색 메서드
    public Page<RecipeEsDto> searchRecipes(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);

        try {
            var query = keyword == null || keyword.isEmpty()
                    ? QueryBuilders.functionScore(fs -> fs
                    .functions(f -> f.randomScore(rs -> rs.seed(String.valueOf(System.currentTimeMillis()))))
            )
                    : QueryBuilders.match(m -> m
                    .field("ingredients")
                    .query(keyword)
            );

            SearchResponse<RecipeES> response = elasticsearchClient.search(s -> s
                            .index("recipes")
                            .query(query)
                            .from((int) pageable.getOffset())
                            .size(pageable.getPageSize()),
                    RecipeES.class);

            List<RecipeEsDto> results = response.hits().hits().stream()
                    .map(hit -> RecipeEsDto.builder()
                            .id(hit.source().getId())
                            .name(hit.source().getName())
                            .recipeImageUrl(hit.source().getRecipeImageUrl())
                            .ingredients(hit.source().getIngredients())
                            .ingredientsTag(hit.source().getIngredientsTag())
                            .matchScore(hit.score().floatValue())
                            .build()
                    )
                    .toList();

            return new PageImpl<>(results, pageable, response.hits().total().value());

        } catch (Exception e) {
            log.error("검색 중 오류 발생: {}", e.getMessage());
            return Page.empty(pageable);
        }
    }
}