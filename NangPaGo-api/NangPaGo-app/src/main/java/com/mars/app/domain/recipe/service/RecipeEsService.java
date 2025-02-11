package com.mars.app.domain.recipe.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldSort;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.*;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mars.app.domain.recipe.dto.RecipeEsListResponseDto;
import com.mars.app.domain.recipe.dto.RecipeSearchResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.exception.NPGExceptionType;
import com.mars.app.domain.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.recipe.builder.EsRecipeSearchQueryBuilder;
import com.mars.app.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.common.model.recipe.RecipeEs;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
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

    public PageResponseDto<RecipeEsListResponseDto> searchRecipes(
        Long userId,
        String keyword,
        String searchType,
        PageRequestVO pageRequestVO
        ) {

        Pageable pageable = pageRequestVO.toPageable();

        EsRecipeSearchQueryBuilder queryBuilder = new EsRecipeSearchQueryBuilder();
        Query query = queryBuilder.buildSearchQuery(keyword, searchType);

        SearchResponse<RecipeEs> response = getRecipeEsSearchResponse(query, pageable);
        List<RecipeEsListResponseDto> results = getResponseDto(response, userId);

        long total = Optional.ofNullable(response.hits().total())
            .map(totalHits -> Optional.of(totalHits.value()).orElse(0L))
            .orElse(0L);

        int totalPages = (int) Math.ceil((double) total / pageable.getPageSize());

        return new PageResponseDto<>(
            results,
            pageable.getPageNumber(),
            totalPages,
            total,
            response.hits().hits().size() < pageable.getPageSize()
        );
    }

    private SearchResponse<RecipeEs> getRecipeEsSearchResponse(Query query, Pageable pageable) {
        try {
            return elasticsearchClient.search(s -> s
                .index("recipes")
                .query(query)
                .highlight(h -> h
                    .fields("name.ngram", f -> f)
                    .preTags("<em>")
                    .postTags("</em>")
                )
                .sort(sort -> sort
                    .field(FieldSort.of(f -> f
                        .field("_score")
                        .order(SortOrder.Desc)
                    ))
                )
                .from((int) pageable.getOffset())
                .size(pageable.getPageSize()), RecipeEs.class);
        } catch (Exception e) {
            throw NPGExceptionType.SERVER_ERROR_ELASTICSEARCH.of("레시피 검색 오류 " + e.getMessage());
        }
    }

    private List<RecipeEsListResponseDto> getResponseDto(SearchResponse<RecipeEs> response, Long userId) {
        List<RecipeLike> recipeLikes = getRecipeLikesBy(userId);

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

                return RecipeEsListResponseDto.of(
                    source,
                    highlightedName,
                    likeCount,
                    commentCount,
                    recipeLikes
                );
            })
            .toList();
    }

    private List<RecipeEsResponseDto> getResponseDtoDetail(SearchResponse<RecipeEs> response, Long userId) { // TODO: MatchScore 보여줄 것인지
        List<RecipeLike> recipeLikes = getRecipeLikesBy(userId);

        return response.hits().hits().stream()
            .filter(hit -> hit.source() != null)
            .map(hit -> {
                RecipeEs source = hit.source();
                String highlightedName = Optional.ofNullable(hit.highlight())
                    .map(highlight -> highlight.get("name.ngram"))
                    .filter(highlights -> !highlights.isEmpty())
                    .map(highlights -> highlights.get(0))
                    .orElse(source.getName());

                float matchScore = hit.score() != null ? hit.score().floatValue() : 0.0f;
                int likeCount = getRecipeLikeCountBy(source.getId());
                int commentCount = getRecipeCommentCountBy(source.getId());

                return RecipeEsResponseDto.of(
                    source,
                    highlightedName,
                    likeCount,
                    commentCount,
                    recipeLikes,
                    matchScore
                );
            })
            .toList();
    }

    private List<RecipeLike> getRecipeLikesBy(Long userId) {
        return userId.equals(User.ANONYMOUS_USER_ID)
            ? new ArrayList<>()
            : recipeLikeRepository.findRecipeLikesByUserId(userId);
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

    public Page<RecipeSearchResponseDto> searchRecipeByKeyword(PageRequestVO pageRequestVO, String keyword, String searchType) {
        Pageable pageable = pageRequestVO.toPageable();

        // 쿼리 빌더 기존꺼 계속 사용
        EsRecipeSearchQueryBuilder queryBuilder = new EsRecipeSearchQueryBuilder();
        Query query = queryBuilder.buildSearchQuery(keyword, searchType);

        //하이라이트 추가해서 Elasticsearch 검색 실행
        SearchResponse<RecipeEs> response = getRecipeEsSearchResponse(query, pageable);

        // 결과를 검색 dto로 매핑
        List<RecipeSearchResponseDto> dtos = response.hits().hits().stream()
            .filter(hit -> hit.source() != null)
            .map(hit -> {
                RecipeEs source = hit.source();
                String highlightedName = Optional.ofNullable(hit.highlight())
                    .map(highlight -> highlight.get("name.ngram"))
                    .filter(highlights -> !highlights.isEmpty())
                    .map(highlights -> highlights.get(0))
                    .orElse(source.getName());
                return RecipeSearchResponseDto.from(source, highlightedName);
            })
            .toList();

        long total = Optional.ofNullable(response.hits().total())
            .map(totalHits -> Optional.of(totalHits.value()).orElse(0L))
            .orElse(0L);

        return new PageImpl<>(dtos, pageable, total);
    }
}
