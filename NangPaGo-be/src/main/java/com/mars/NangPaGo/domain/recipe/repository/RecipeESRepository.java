package com.mars.NangPaGo.domain.recipe.repository;

import com.mars.NangPaGo.domain.recipe.entity.RecipeEs;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface RecipeESRepository extends ElasticsearchRepository<RecipeEs, String> {
}
