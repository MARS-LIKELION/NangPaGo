package com.mars.NangPaGo.domain.recipe.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "recipes")
public class RecipeES {

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
