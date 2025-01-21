package com.mars.app.domain.recipe.builder;

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

    private Query createKeywordSearchQuery(String keyword, String searchType) {
        if ("NAME".equals(searchType)) {
            return getQueryByNameWithNgram(keyword);
        }

        return getQueryByIngredients(keyword);
    }

    private Query getQueryByIngredients(String keyword) {
        return QueryBuilders.bool(b -> b
            .should(
                QueryBuilders.match(m -> m
                    .field("ingredients")
                    .query(keyword)
                )
            )
        );
    }

    private Query getQueryByNameWithNgram(String keyword) {
        return QueryBuilders.bool(b -> b
            .must(m -> m
                .match(mm -> mm
                    .field("name.jaso")
                    .query(keyword)
                    .analyzer("suggest_search_analyzer")
                )
            )
            .should(s -> s
                .match(mm -> mm
                    .field("name.ngram")
                    .query(keyword)
                    .analyzer("my_ngram_analyzer")
                )
            )
        );
    }

    private Query createRandomSearchQuery() {
        return QueryBuilders.functionScore(fs -> fs
            .query(QueryBuilders.matchAll(m -> m))
            .functions(f -> f
                .randomScore(rs -> rs
                    .seed(String.valueOf(System.currentTimeMillis()))
                    .field("_seq_no")
                )
            )
        );
    }
}
