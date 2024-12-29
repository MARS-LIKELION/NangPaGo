package com.mars.NangPaGo.domain.ingredient.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ingredients_dictionary")
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    private String name;

    @Builder
    private Ingredient(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public static Ingredient create(Long id, String name) {
        return Ingredient.builder()
            .id(id)
            .name(name)
            .build();
    }
}
