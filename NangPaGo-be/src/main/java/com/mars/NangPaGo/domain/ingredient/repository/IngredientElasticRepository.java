package com.mars.NangPaGo.domain.ingredient.repository;

import com.mars.NangPaGo.domain.ingredient.entity.IngredientElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface IngredientElasticRepository extends ElasticsearchRepository<IngredientElastic, String> {
}
