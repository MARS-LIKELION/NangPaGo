package com.mars.NangPaGo.domain.recipe.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.recipe.builder.EsRecipeSearchQueryBuilder;
import com.mars.NangPaGo.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.NangPaGo.domain.recipe.entity.RecipeEs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class RecipeEsService {

    private final ElasticsearchClient elasticsearchClient;

    public Page<RecipeEsResponseDto> searchRecipes(int page, int size, String keyword, String searchType) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        EsRecipeSearchQueryBuilder queryBuilder = new EsRecipeSearchQueryBuilder();
        Query query = queryBuilder.buildSearchQuery(keyword, searchType);

        SearchResponse<RecipeEs> response = getRecipeEsSearchResponse(query, pageable);
        List<RecipeEsResponseDto> results = response.hits().hits().stream()
            .filter(hit -> hit.source() != null)
            .map(hit -> RecipeEsResponseDto.of(
                hit.source(),
                hit.score() != null ? hit.score().floatValue() : 0.0f
            ))
            .toList();

        long total = Optional.ofNullable(response.hits().total())
            .map(totalHits -> Optional.of(totalHits.value()).orElse(0L))
            .orElse(0L);

        return new PageImpl<>(results, pageable, total);

    }

    private SearchResponse<RecipeEs> getRecipeEsSearchResponse(Query query, Pageable pageable) {
        try {
            return elasticsearchClient.search(s -> s
                .index("recipes")
                .query(query)
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()), RecipeEs.class);
        } catch (Exception e) {
            throw NPGExceptionType.SERVER_ERROR_ELASTICSEARCH.of("레시피 검색 오류 " + e.getMessage());
        }
    }
}
