package com.mars.common.model.userRecipe;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRecipeManualImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_recipe_manual_id", nullable = false)
    private UserRecipeManual userRecipeManual;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String imageUrl; // 조리 과정 이미지 URL

    public static UserRecipeManualImage of(UserRecipeManual userRecipeManual, String imageUrl) {
        return UserRecipeManualImage.builder()
            .userRecipeManual(userRecipeManual)
            .imageUrl(imageUrl)
            .build();
    }
}
