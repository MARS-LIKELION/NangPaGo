package com.mars.NangPaGo.domain.recipe.repository;

import com.mars.NangPaGo.domain.recipe.entity.RecipeES;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;


public interface RecipeESRepository extends ElasticsearchRepository<RecipeES, String> {
    Page<RecipeES> findAll(Pageable pageable);
}