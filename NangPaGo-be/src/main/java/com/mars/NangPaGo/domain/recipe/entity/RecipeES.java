package com.mars.NangPaGo.domain.recipe.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Getter
@Document(indexName = "recipes")
public class RecipeES {

    public RecipeES(String id, String name, List<String> ingredients) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
    }

    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Field(type = FieldType.Keyword)
    private List<String> ingredients;
}

