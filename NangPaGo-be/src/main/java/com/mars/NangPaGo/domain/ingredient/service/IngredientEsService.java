package com.mars.NangPaGo.domain.ingredient.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.SERVER_ERROR_ELASTICSEARCH;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mars.NangPaGo.domain.ingredient.dto.IngredientEsResponseDto;
import com.mars.NangPaGo.domain.ingredient.entity.IngredientEs;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
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

                    Map<String, List<String>> highlightFields = hit.highlight();
                    List<String> highlightNames = highlightFields != null ? highlightFields.get("name.ngram") : null;

                    String highlightedName = (highlightNames != null && !highlightNames.isEmpty())
                        ? highlightNames.get(0) : source.getName();

                    return IngredientEsResponseDto.of(source, highlightedName, matchScore);
                })
                .sorted(Comparator.comparingDouble(IngredientEsResponseDto::matchScore).reversed())
                .collect(Collectors.toList());

        } catch (IOException e) {
            throw SERVER_ERROR_ELASTICSEARCH.of("ingredients_dictionary 인덱스 접근 에러");
        }
    }
}
