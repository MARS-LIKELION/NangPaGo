package com.mars.app.domain.recipe.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mars.app.common.exception.NPGExceptionType;
import com.mars.app.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.recipe.builder.EsRecipeSearchQueryBuilder;
import com.mars.app.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.app.domain.recipe.entity.RecipeEs;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
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
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeCommentRepository recipeCommentRepository;

    public Page<RecipeEsResponseDto> searchRecipes(int page, int size, String keyword, String searchType) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), Math.max(size, 1));

        EsRecipeSearchQueryBuilder queryBuilder = new EsRecipeSearchQueryBuilder();
        Query query = queryBuilder.buildSearchQuery(keyword, searchType);

        SearchResponse<RecipeEs> response = getRecipeEsSearchResponse(query, pageable);
        List<RecipeEsResponseDto> results = getResponseDtos(response);

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
                .highlight(h -> h
                    .fields("name.ngram", f -> f)
                    .preTags("<em>").postTags("</em>")
                )
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()), RecipeEs.class);
        } catch (Exception e) {
            throw NPGExceptionType.SERVER_ERROR_ELASTICSEARCH.of("레시피 검색 오류 " + e.getMessage());
        }
    }

    private List<RecipeEsResponseDto> getResponseDtos(SearchResponse<RecipeEs> response) {
        return response.hits().hits().stream()
            .filter(hit -> hit.source() != null)
            .map(hit -> {
                RecipeEs source = hit.source();
                String highlightedName = Optional.ofNullable(hit.highlight())
                    .map(highlight -> highlight.get("name.ngram"))
                    .filter(highlights -> !highlights.isEmpty())
                    .map(highlights -> highlights.get(0))
                    .orElse(source.getName());

                int likeCount = getRecipeLikeCountBy(source.getId());
                int commentCount = getRecipeCommentCountBy(source.getId());

                return RecipeEsResponseDto.of(
                    source,
                    highlightedName,
                    likeCount,
                    commentCount,
                    hit.score() != null ? hit.score().floatValue() : 0.0f
                );
            })
            .toList();
    }

    private int getRecipeLikeCountBy(String recipeId) {
        try {
            return recipeLikeRepository.countByRecipeId(Long.parseLong(recipeId));
        } catch (Exception e) {
            throw NPGExceptionType.SERVER_ERROR.of("String type recipeId parse to Long is failed (recipeLike)");
        }
    }

    private int getRecipeCommentCountBy(String recipeId) {
        try {
            return recipeCommentRepository.countByRecipeId(Long.parseLong(recipeId));
        } catch (Exception e) {
            throw NPGExceptionType.SERVER_ERROR.of("String type recipeId parse to Long is failed (recipeComment)");
        }
    }
}
