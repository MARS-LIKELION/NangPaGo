package com.mars.NangPaGo.domain.ingredient.entity;

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

    @Field(type = FieldType.Text)
    private String name;

    public static IngredientEs of(String id, String name) {
        return IngredientEs.builder()
            .id(id)
            .name(name)
            .build();
    }

    public static IngredientEs of(Long id, String name) {
        return IngredientEs.builder()
            .id(String.valueOf(id))
            .name(name)
            .build();
    }
}
