package com.mars.NangPaGo.domain.recipe.builder;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;

import java.util.Optional;

public class EsRecipeSearchQueryBuilder {
    public Query buildSearchQuery(String keyword, String searchType) {
        return Optional.ofNullable(keyword)
            .filter(k -> !k.isEmpty())
            .map(k -> createKeywordSearchQuery(k, searchType))
            .orElseGet(this::createRandomSearchQuery);
    }

    private Query createRandomSearchQuery() {
        return QueryBuilders.functionScore(fs -> fs
            .query(QueryBuilders.matchAll(m -> m))
            .functions(f -> f
                .randomScore(rs -> rs
                    .seed(String.valueOf(System.currentTimeMillis()))
                )
            )
        );
    }

    private Query createKeywordSearchQuery(String keyword, String searchType) {
        String searchField = "name".equals(searchType) ? "name" : "ingredients";
        return QueryBuilders.bool(b -> b
            .should(
                QueryBuilders.match(m -> m
                    .field(searchField)
                    .query(keyword)
                )
            )
        );
    }
}
