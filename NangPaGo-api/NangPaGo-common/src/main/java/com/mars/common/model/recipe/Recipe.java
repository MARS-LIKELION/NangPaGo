package com.mars.common.model.recipe;

import com.mars.common.model.BaseEntity;
import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.common.model.favorite.recipe.RecipeFavorite;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    private String name;

    @Column(name = "ingredient_detail", columnDefinition = "TEXT")
    private String ingredients;

    private String cookingMethod;
    private String category;
    private Integer calorie;
    private Integer fat;
    private Integer carbohydrate;
    private Integer protein;
    private Integer natrium;
    private String mainIngredient;
    private String mainImage;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Manual> manuals;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ManualImage> manualImages;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComment> comments;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeLike> likes;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeFavorite> favorites;

    public void updateMainImage(String newImageUrl) {
        this.mainImage = newImageUrl;
    }
}
