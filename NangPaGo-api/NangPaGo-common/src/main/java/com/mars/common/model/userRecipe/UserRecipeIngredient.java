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
    private String name;

    @Column(nullable = false)
    private String amount;

    public static UserRecipeIngredient of(UserRecipe userRecipe, String name, String amount) {
        return UserRecipeIngredient.builder()
            .userRecipe(userRecipe)
            .name(name)
            .amount(amount)
            .build();
    }
}
