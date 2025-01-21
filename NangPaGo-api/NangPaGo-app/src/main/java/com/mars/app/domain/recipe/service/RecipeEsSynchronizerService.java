package com.mars.app.domain.recipe.service;

import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeEs;
import com.mars.app.domain.recipe.repository.RecipeESRepository;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;


@RequiredArgsConstructor
@Service
public class RecipeEsSynchronizerService {

    private final RecipeRepository recipeRepository;
    private final RecipeESRepository recipeEsRepository;

    @Transactional
    public String insertRecipeFromMysql() {
        try {
            recipeEsRepository.deleteAll();

            List<Recipe> recipeList = recipeRepository.findAll();

            if (recipeList.isEmpty()) {
                throw NPGExceptionType.NOT_FOUND_RECIPE.of("MySQL에서 가져온 Recipe 데이터가 없습니다.");
            }

            List<RecipeEs> recipeElasticList = new ArrayList<>();
            for (Recipe recipe : recipeList) {
                List<String> ingredientsList = List.of(recipe.getIngredients().split(",")).stream()
                    .map(String::trim)
                    .filter(ingredient -> !ingredient.isEmpty())
                    .toList();

                List<String> ingredientsTagList = ingredientsList.stream().limit(5).toList();
                List<String> displayTag = new ArrayList<>();

                if (recipe.getMainIngredient() != null && !recipe.getMainIngredient().isBlank()) {
                    displayTag.add(recipe.getMainIngredient());
                }

                if (recipe.getCalorie() != null) {
                    displayTag.add(String.valueOf(recipe.getCalorie() + " kcal"));
                }

                if (recipe.getCategory() != null){
                    displayTag.add(recipe.getCategory());
                }

                if (recipe.getCookingMethod() != null) {
                    displayTag.add(recipe.getCookingMethod());
                }
                int likeCount = recipe.getLikes() != null ? recipe.getLikes().size() : 0;
                int commentCount = recipe.getComments() != null ? recipe.getComments().size() : 0;


                RecipeEs recipeEs = new RecipeEs(
                    String.valueOf(recipe.getId()),
                    recipe.getName(),
                    recipe.getMainImage(),
                    ingredientsList,
                    ingredientsTagList,
                    displayTag,
                    likeCount,
                    commentCount
                );
                recipeElasticList.add(recipeEs);
            }
            recipeEsRepository.saveAll(recipeElasticList);

            return "MySQL로부터 Recipe 데이터를 Elasticsearch에 성공적으로 동기화했습니다!";
        } catch (Exception e) {
            throw NPGExceptionType.SERVER_ERROR_ELASTICSEARCH.of("Elasticsearch 동기화 중 오류 발생: " + e.getMessage());
        }
    }
}
