package com.mars.app.domain.recipe.builder;

import co.elastic.clients.elasticsearch._types.query_dsl.*;

import java.util.Optional;

public class EsRecipeSearchQueryBuilder {
    public Query buildSearchQuery(String keyword, String searchType) {
        return Optional.ofNullable(keyword)
            .filter(k -> !k.isEmpty())
            .map(k -> createKeywordSearchQuery(k, searchType))
            .orElseGet(this::createRandomSearchQuery);
    }

    private Query createKeywordSearchQuery(String keyword, String searchType) {
        return switch (searchType) {
            case "NAME" -> getQueryByNameWithNgram(keyword);
            case "INGREDIENTS" -> getQueryByIngredients(keyword);
            default -> getQueryByNameWithNgram(keyword);
        };
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
                .filter(q -> q.exists(e -> e.field("likes")))
                .fieldValueFactor(ff -> ff
                    .field("likes")
                    .factor(1.0)
                    .missing(0.0)
                )
            )
            .functions(f -> f
                .filter(q -> q.exists(e -> e.field("comments")))
                .fieldValueFactor(ff -> ff
                    .field("comments")
                    .factor(1.5)
                    .missing(0.0)
                )
            )
            .scoreMode(FunctionScoreMode.Sum)
            .boostMode(FunctionBoostMode.Sum)
        );
    }
}
