package com.mars.NangPaGo.domain.recipe.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.indices.CreateIndexRequest;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.RecipeES;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

            CreateIndexRequest request = new CreateIndexRequest.Builder()
                .index("recipes")
                .withJson(new StringReader(indexSettings))
                .build();

            elasticsearchClient.indices().create(request);
            return "Elasticsearch 인덱스가 성공적으로 생성되었습니다!";
        } catch (Exception e) {
            log.error("Elasticsearch 인덱스 생성 중 오류 발생: {}", e.getMessage());
            throw new NPGException(NPGExceptionType.SERVER_ERROR, "Elasticsearch 인덱스 생성 실패");
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
                String id = record.get("RCP_SEQ").trim();
                String name = record.get("RCP_NM").trim();
                String recipeImageUrl = record.get("ATT_FILE_NO_MAIN").trim();
                List<String> ingredientsList = List.of(record.get("RCP_PARTS_DTLS").split(",")).stream()
                    .map(String::trim)
                    .filter(ingredient -> !ingredient.isEmpty())
                    .toList();
                List<String> ingredientsTag = ingredientsList.stream().limit(5).toList();

                RecipeES recipeES = new RecipeES(id, name, recipeImageUrl, ingredientsList, ingredientsTag);
                recipeESList.add(recipeES);
            }

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
            throw new NPGException(NPGExceptionType.BAD_REQUEST, "CSV 데이터 삽입 실패");
        }
    }

    // 검색 메서드
    public Page<RecipeEsResponseDto> searchRecipes(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page - 1, size);

        try {
            var query = (keyword == null || keyword.trim().isEmpty())
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
                .size(pageable.getPageSize()), RecipeES.class);

            List<RecipeEsResponseDto> results = response.hits().hits().stream()
                .map(hit -> RecipeEsResponseDto.of(
                    hit.source(), // RecipeES 엔티티
                    hit.score() != null ? hit.score().floatValue() : 0.0f // matchScore를 가져옴
                ))
                .toList();

            return new PageImpl<>(results, pageable, response.hits().total().value());

        } catch (Exception e) {
            log.error("검색 중 오류 발생: {}", e.getMessage());
            throw new NPGException(NPGExceptionType.SERVER_ERROR, "레시피 검색 실패");
        }
    }
}
