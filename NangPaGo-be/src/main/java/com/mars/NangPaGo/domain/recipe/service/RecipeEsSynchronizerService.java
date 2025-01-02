package com.mars.NangPaGo.domain.recipe.service;

import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.entity.RecipeEs;
import com.mars.NangPaGo.domain.recipe.repository.RecipeESRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@AllArgsConstructor
@Service
public class RecipeEsSynchronizerService {

    private final RecipeRepository recipeRepository;
    private final RecipeESRepository recipeEsRepository;

    @Transactional
    public String insertRecipeFromMysql() {
        try {
            List<Recipe> recipeList = recipeRepository.findAll();

            if (recipeList.isEmpty()) {
                throw new NPGException(NPGExceptionType.NOT_FOUND_RECIPE, "MySQL에서 가져온 Recipe 데이터가 없습니다.");
            }

            List<RecipeEs> recipeElasticList = new ArrayList<>();
            for (Recipe recipe : recipeList) {
                List<String> ingredientsList = List.of(recipe.getIngredients().split(",")).stream()
                    .map(String::trim)
                    .filter(ingredient -> !ingredient.isEmpty())
                    .toList();

                List<String> ingredientsTagList = ingredientsList.stream().limit(5).toList();
                List<String> displayTag = new ArrayList<>();

                // 주재료
                if (recipe.getMainIngredient() != null && !recipe.getMainIngredient().isBlank()) {
                    displayTag.add(recipe.getMainIngredient());
                }

                // 칼로리
                if (recipe.getCalorie() != null) {
                    displayTag.add(String.valueOf(recipe.getCalorie()));
                }

                // 카테고리
                if (recipe.getCategory() != null && !recipe.getCategory().isBlank()) {
                    displayTag.add(recipe.getCategory());
                }

                // 쿠킹 메서드
                if (recipe.getCookingMethod() != null && !recipe.getCookingMethod().equalsIgnoreCase("기타")) {
                    displayTag.add(recipe.getCookingMethod());
                }

                RecipeEs recipeES = new RecipeEs(
                    String.valueOf(recipe.getId()),
                    recipe.getName(),
                    recipe.getMainImage(),
                    ingredientsList,
                    ingredientsTagList,
                    displayTag
                );
                recipeElasticList.add(recipeES);
            }

            recipeEsRepository.saveAll(recipeElasticList);

            return "MySQL로부터 Recipe 데이터를 Elasticsearch에 성공적으로 동기화했습니다!";
        } catch (NPGException e) {
            throw e;
        } catch (Exception e) {
            throw new NPGException(NPGExceptionType.SERVER_ERROR_ELASTICSEARCH, "Elasticsearch 동기화 중 오류 발생: " + e.getMessage());
        }
    }
}