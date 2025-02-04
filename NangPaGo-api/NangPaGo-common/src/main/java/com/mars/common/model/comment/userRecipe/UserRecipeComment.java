package com.mars.common.model.comment.userRecipe;

import com.mars.common.enums.comment.UserRecipeCommentStatus;
import com.mars.common.model.BaseEntity;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.user.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserRecipeComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_recipe_id", nullable = false)
    private UserRecipe userRecipe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "comment_status", nullable = false)
    private UserRecipeCommentStatus commentStatus;

    public static UserRecipeComment create(UserRecipe userRecipe, User user, String content) {
        return UserRecipeComment.builder()
            .userRecipe(userRecipe)
            .user(user)
            .content(content)
            .commentStatus(UserRecipeCommentStatus.ACTIVE)
            .build();
    }

    public void updateText(String content) {
        this.content = content;
    }

    public void softDelete(){
        this.commentStatus = UserRecipeCommentStatus.DELETED;
    }
}
