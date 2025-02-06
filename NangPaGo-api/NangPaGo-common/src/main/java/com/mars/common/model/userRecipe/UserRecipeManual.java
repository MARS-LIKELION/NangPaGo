package com.mars.common.model.userRecipe;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRecipeManual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_recipe_id", nullable = false)
    private UserRecipe userRecipe;

    @Column(nullable = false)
    private Integer step;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(columnDefinition = "TEXT", nullable = true)
    private String imageUrl;

    public static UserRecipeManual of(UserRecipe userRecipe, Integer step, String description, String imageUrl) {
        return UserRecipeManual.builder()
            .userRecipe(userRecipe)
            .step(step)
            .description(description)
            .imageUrl(imageUrl)
            .build();
    }

    public void update(String description, String imageUrl) {
        this.description = description;
        this.imageUrl = imageUrl;
    }
}
