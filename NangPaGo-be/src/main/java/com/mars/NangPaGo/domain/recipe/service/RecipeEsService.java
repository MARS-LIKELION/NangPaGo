package com.mars.NangPaGo.domain.recipe.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.RecipeEs;
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
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeEsService {

    private final ElasticsearchClient elasticsearchClient;

    public String insertRecipesFromCsv(MultipartFile file) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), "UTF-8"))) {
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreSurroundingSpaces()
                    .withTrim());

            List<RecipeEs> recipeEsList = new ArrayList<>();
            for (CSVRecord record : csvParser) {
                String id = record.get("id").trim();
                String name = record.get("name").trim();
                String recipeImageUrl = record.get("main_image").trim();
                String recipeDescription = record.get("recipe_description").trim();

                if (id == null || id.isEmpty()) {
                    continue;
                }

                List<String> ingredientsList = List.of(record.get("ingredient_detail").split(",")).stream()
                    .map(String::trim)
                    .filter(ingredient -> !ingredient.isEmpty())
                    .toList();
                List<String> ingredientsTag = ingredientsList.stream().limit(5).toList();

                RecipeEs recipeES = RecipeEs.builder()
                    .id(id)
                    .name(name)
                    .recipeImageUrl(recipeImageUrl)
                    .ingredients(ingredientsList)
                    .ingredientsTag(ingredientsTag)
                    .ingredientsDisplayTag(null)
                    .build();
                recipeEsList.add(recipeES);
            }

            for (RecipeEs recipe : recipeEsList) {
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

    public Page<RecipeEsResponseDto> searchRecipes(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1));

        try {
            var query = (keyword == null || keyword.isEmpty())
                ? QueryBuilders.matchAll()
                : QueryBuilders.match(m -> m
                .field("ingredients")
                .query(keyword)
            );

            log.info("Elasticsearch 검색 쿼리: {}", query);

            SearchResponse<RecipeEs> response = elasticsearchClient.search(s -> s
                .index("recipes")
                .query((Query) query)
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()), RecipeEs.class);

            log.info("Elasticsearch 응답: {}", response);

            List<RecipeEsResponseDto> results = response.hits().hits().stream()
                .map(hit -> RecipeEsResponseDto.of(
                    hit.source(),
                    hit.score() != null ? hit.score().floatValue() : 0.0f
                ))
                .toList();

            return new PageImpl<>(results, pageable, response.hits().total().value());
        } catch (Exception e) {
            log.error("검색 중 오류 발생: ", e);
            throw new NPGException(NPGExceptionType.SERVER_ERROR, "레시피 검색 실패");
        }
    }

}
