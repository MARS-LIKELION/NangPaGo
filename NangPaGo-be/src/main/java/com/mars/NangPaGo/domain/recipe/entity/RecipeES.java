package com.mars.NangPaGo.domain.recipe.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Document(indexName = "recipes")
public class RecipeES {

    public RecipeES() {
    }

    public RecipeES(String id, String name, String recipeImageUrl, List<String> ingredients, List<String> ingredientsTag) {
        this.id = id;
        this.name = name;
        this.recipeImageUrl = recipeImageUrl;
        this.ingredients = ingredients;
        this.ingredientsTag = ingredientsTag;
    }

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Text)
    private String recipeImageUrl;

    @Field(type = FieldType.Text)
    private List<String> ingredients;

    @Field(type = FieldType.Text)
    private List<String> ingredientsTag;
}
