package com.mars.common.model.recipe;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@Document(indexName = "recipes")
public class RecipeEs {

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

    @Field(type = FieldType.Text)
    private List<String> ingredientsDisplayTag;

    @Field(type = FieldType.Integer)
    private Integer likes;

    @Field(type = FieldType.Integer)
    private Integer comments;

}
