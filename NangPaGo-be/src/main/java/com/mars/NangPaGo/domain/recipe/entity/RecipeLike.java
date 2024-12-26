package com.mars.NangPaGo.domain.recipe.entity;

import com.mars.NangPaGo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
public class RecipeLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
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
