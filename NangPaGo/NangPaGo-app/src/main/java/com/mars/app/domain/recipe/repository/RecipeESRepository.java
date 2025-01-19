package com.mars.app.domain.recipe.repository;

import com.mars.app.domain.recipe.entity.RecipeEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface RecipeESRepository extends ElasticsearchRepository<RecipeEs, String> {
}
