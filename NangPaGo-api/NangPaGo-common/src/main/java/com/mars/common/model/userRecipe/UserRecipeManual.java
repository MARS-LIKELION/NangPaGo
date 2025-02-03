package com.mars.common.model.userRecipe;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

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
    private Integer step; // 단계 번호

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description; // 설명

    @OneToMany(mappedBy = "userRecipeManual", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserRecipeManualImage> images = new ArrayList<>();

    public static UserRecipeManual of(UserRecipe userRecipe, Integer step, String description) {
        return UserRecipeManual.builder()
            .userRecipe(userRecipe)
            .step(step)
            .description(description)
            .images(new ArrayList<>())
            .build();
    }

    public void addImage(UserRecipeManualImage image) {
        this.images.add(image);
    }
}
