package com.mars.NangPaGo.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class RecipeEsDto {
    private String id;
    private String name;
    private String recipeImageUrl;
    private List<String> ingredients;
    private List<String> ingredientsTag;
    private float matchScore;
}
