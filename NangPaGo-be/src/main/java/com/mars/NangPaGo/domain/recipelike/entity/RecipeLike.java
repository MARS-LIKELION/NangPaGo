package com.mars.NangPaGo.domain.recipelike.entity;

import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

@Getter
@Entity
public class RecipeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Recipe recipe;

    @Builder
    private RecipeLike(User user, Recipe recipe) {
        this.user = user;
        this.recipe = recipe;
    }

    public static RecipeLike of(User user, Recipe recipe) {
        return RecipeLike.builder()
                .user(user)
                .recipe(recipe)
                .build();
    }
}
