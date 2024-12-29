package com.mars.NangPaGo.domain.ingredient.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.ingredient.entity.IngredientElastic;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class IngredientElasticSearchService {
    private final ElasticsearchClient elasticsearchClient;

    public IngredientElasticSearchService(ElasticsearchClient elasticsearchClient) {
        this.elasticsearchClient = elasticsearchClient;
    }

    public List<IngredientElastic> searchByPrefix(String keyword) {
        try {
            SearchResponse<IngredientElastic> response = elasticsearchClient.search(
                s -> s.index("ingredients_dictionary")
                    .query(q -> q.prefix(p -> p.field("name").value(keyword))), // 접두어 검색
                IngredientElastic.class
            );

            return response.hits().hits().stream()
                .map(hit -> hit.source())
                .collect(Collectors.toList());
        } catch (IOException e) {
            throw NPGExceptionType.SERVER_ERROR_ELASTICSEARCH.of("ingredients_dictionary 인덱스 접근 에러");
        }
    }
}
