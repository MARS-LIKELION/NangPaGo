package com.mars.app.domain.ingredient.service;

import static com.mars.app.common.exception.NPGExceptionType.SERVER_ERROR_ELASTICSEARCH;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mars.app.domain.ingredient.dto.IngredientEsResponseDto;
import com.mars.app.domain.ingredient.entity.IngredientEs;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class IngredientEsService {
    private final ElasticsearchClient elasticsearchClient;

    public IngredientEsService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<IngredientEsResponseDto> searchIngredients(String keyword) {
        try {
            SearchResponse<IngredientEs> response = elasticsearchClient.search(
                s -> s.index("ingredients_dictionary")
                    .query(q -> q.bool(b -> b
                        .must(m -> m.match(mm -> mm
                            .field("name.jaso")
                            .query(keyword)
                            .analyzer("suggest_search_analyzer")
                        ))
                        .should(should -> should.match(ss -> ss
                            .field("name.ngram")
                            .query(keyword)
                            .analyzer("my_ngram_analyzer")
                        ))
                    ))
                    .highlight(h -> h
                        .fields("name.ngram", f -> f)
                        .preTags("<em>").postTags("</em>")
                    )
//                    .from(0)  // 필요시 추가. (0: 첫번째 페이지)
//                    .size(10) // 필요시 추가. (10: 10개 항목만 반환)
                    .sort(so -> so
                        .field(f -> f
                            .field("_score")
                            .order(SortOrder.Desc)
                        )
                    ),
                IngredientEs.class
            );

            return response.hits().hits().stream()
                .map(hit -> {
                    IngredientEs source = hit.source();
                    double matchScore = hit.score();
                    String highlightedName = Optional.ofNullable(hit.highlight())
                        .map(highlightFields -> highlightFields.get("name.ngram"))
                        .filter(highlightList -> !highlightList.isEmpty())
                        .map(highlightList -> highlightList.get(0))
                        .orElse(source.getName());

                    return IngredientEsResponseDto.of(source, highlightedName, matchScore);
                })
                .collect(Collectors.toList());

        } catch (IOException e) {
            throw SERVER_ERROR_ELASTICSEARCH.of("ingredients_dictionary 인덱스 접근 에러");
        }
    }
}
