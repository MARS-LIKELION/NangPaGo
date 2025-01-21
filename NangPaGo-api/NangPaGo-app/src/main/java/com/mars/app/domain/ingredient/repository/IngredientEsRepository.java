package com.mars.app.domain.ingredient.repository;

import com.mars.common.model.ingredient.IngredientEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IngredientEsRepository extends ElasticsearchRepository<IngredientEs, String> {
}
