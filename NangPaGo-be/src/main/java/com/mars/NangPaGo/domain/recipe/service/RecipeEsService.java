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
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class RecipeEsService {

    private final ElasticsearchClient elasticsearchClient;

    public Page<RecipeEsResponseDto> searchRecipes(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(Math.max(page - 1, 0), Math.max(size, 1));

        try {
            var query = (keyword == null || keyword.isEmpty())
                ? QueryBuilders.matchAll()
                : QueryBuilders.match(m -> m
                .field("ingredients")
                .query(keyword)
            );

            SearchResponse<RecipeEs> response = elasticsearchClient.search(s -> s
                .index("recipes")
                .query((Query) query)
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()), RecipeEs.class);

            List<RecipeEsResponseDto> results = response.hits().hits().stream()
                .map(hit -> RecipeEsResponseDto.of(
                    hit.source(),
                    hit.score() != null ? hit.score().floatValue() : 0.0f
                ))
                .toList();

            return new PageImpl<>(results, pageable, response.hits().total().value());
        } catch (Exception e) {
            throw new NPGException(NPGExceptionType.SERVER_ERROR, "레시피 검색 실패");
        }
    }

}
