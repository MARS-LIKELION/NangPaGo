package com.mars.app.domain.ingredient.repository;

import com.mars.app.domain.ingredient.entity.IngredientEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IngredientEsRepository extends ElasticsearchRepository<IngredientEs, String> {
}
