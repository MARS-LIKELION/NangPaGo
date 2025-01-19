package com.mars.common.model.ingredient;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Setting(settingPath = "elastic/ingredient/post-setting.json")
@Mapping(mappingPath = "elastic/ingredient/post-mapping.json")
@Document(indexName = "ingredients_dictionary", writeTypeHint = WriteTypeHint.FALSE)
public class IngredientEs {

    @Id
    private String id;

    @JsonProperty("ingredient_id")
    @Field(type = FieldType.Long, name = "ingredient_id")
    private Long ingredientId;

    @Field(type = FieldType.Text)
    private String name;

    public static IngredientEs of(Long ingredientId, String name) {
        return IngredientEs.builder()
            .ingredientId(ingredientId)
            .name(name)
            .build();
    }

    public static IngredientEs of(String ingredientId, String name) {
        return IngredientEs.builder()
            .ingredientId(Long.valueOf(ingredientId))
            .name(name)
            .build();
    }
}
