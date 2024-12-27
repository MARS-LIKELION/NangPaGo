package com.mars.NangPaGo.domain.ingredient.entity;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Getter
@Builder
@Document(indexName = "ingredients_dictionary")
public class IngredientElastic {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String name;

    @Builder
    private IngredientElastic(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static IngredientElastic create(String id, String name) {
        return IngredientElastic.builder()
            .id(id)
            .name(name)
            .build();
    }

    public static IngredientElastic create(Long id, String name) {
        return IngredientElastic.builder()
            .id(String.valueOf(id))
            .name(name)
            .build();
    }
}
