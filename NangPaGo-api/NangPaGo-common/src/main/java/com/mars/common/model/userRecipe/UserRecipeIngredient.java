package com.mars.common.model.userRecipe;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_recipe_id", nullable = false)
    private UserRecipe userRecipe;

    @Column(nullable = false)
    private String name; // 재료 이름

    public static UserRecipeIngredient of(UserRecipe userRecipe, String name) {
        return UserRecipeIngredient.builder()
            .userRecipe(userRecipe)
            .name(name)
            .build();
    }
}
