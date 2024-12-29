package com.mars.NangPaGo.domain.ingredient.entity;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setting(settingPath = "elastic/ingredient/post-setting.json")
@Mapping(mappingPath = "elastic/ingredient/post-mapping.json")
@Document(indexName = "ingredients_dictionary", writeTypeHint = WriteTypeHint.FALSE)
public class IngredientElastic {
    @Id
    private Long id;

    @Field(type = FieldType.Text)
    private String name;

    @Builder
    private IngredientElastic(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static IngredientElastic create(String id, String name) {
        return IngredientElastic.builder()
            .id(Long.valueOf(id))
            .name(name)
            .build();
    }

    public static IngredientElastic create(Long id, String name) {
        return IngredientElastic.builder()
            .id(id)
            .name(name)
            .build();
    }
}
