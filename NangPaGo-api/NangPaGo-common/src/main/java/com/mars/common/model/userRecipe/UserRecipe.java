package com.mars.common.model.userRecipe;

import com.mars.common.enums.userRecipe.UserRecipeStatus;
import com.mars.common.model.BaseEntity;
import com.mars.common.model.comment.userRecipe.UserRecipeComment;
import com.mars.common.model.user.User;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRecipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String mainImageUrl;

    @OneToMany(mappedBy = "userRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    @OrderBy("step ASC")
    private List<UserRecipeManual> manuals = new ArrayList<>();

    @OneToMany(mappedBy = "userRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserRecipeIngredient> ingredients = new ArrayList<>();

    @OneToMany(mappedBy = "userRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRecipeComment> comments;

    @OneToMany(mappedBy = "userRecipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserRecipeLike> likes;

    @Enumerated(EnumType.STRING)
    @Column(name = "recipe_status", nullable = false)
    private UserRecipeStatus recipeStatus;

    public static UserRecipe create(User user,
                                String title,
                                String content,
                                String mainImageUrl,
                                boolean isPublic,
                                List<UserRecipeIngredient> ingredients) {
        return UserRecipe.builder()
            .user(user)
            .title(title)
            .content(content)
            .mainImageUrl(mainImageUrl)
            .isPublic(isPublic)
            .ingredients(ingredients)
            .recipeStatus(UserRecipeStatus.ACTIVE)
            .build();
    }

    public void update(String title, String content, boolean isPublic, String mainImageUrl) {
        this.title = title;
        this.content = content;
        this.isPublic = isPublic;
        this.mainImageUrl = mainImageUrl;
    }

    public boolean isPrivate() {
        return !this.isPublic;
    }

    public void softDelete(){
        this.recipeStatus = UserRecipeStatus.DELETED;
    }
}
