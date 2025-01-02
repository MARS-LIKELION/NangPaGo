package com.mars.NangPaGo.domain.ingredient.repository;

import com.mars.NangPaGo.domain.ingredient.entity.IngredientEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IngredientEsRepository extends ElasticsearchRepository<IngredientEs, String> {
}
