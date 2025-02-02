package com.mars.common.model.userCommunity;

import com.mars.common.model.BaseEntity;
import com.mars.common.model.comment.userRecipe.UserCommunityComment;
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
public class UserCommunity extends BaseEntity {

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
    private String mainImageUrl; // 대표 이미지 (한 장만)

    @OneToMany(mappedBy = "userCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommunityManual> manuals; // 조리 과정 리스트

    @OneToMany(mappedBy = "userCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommunityIngredient> ingredients; // 재료 리스트

    @OneToMany(mappedBy = "userCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommunityComment> comments; // 댓글

    @OneToMany(mappedBy = "userCommunity", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserCommunityLike> likes; // 좋아요

    public static UserCommunity of(User user,
                                   String title,
                                   String content,
                                   String mainImageUrl,
                                   boolean isPublic,
                                   List<UserCommunityIngredient> ingredients) {
        return UserCommunity.builder()
            .user(user)
            .title(title)
            .content(content)
            .mainImageUrl(mainImageUrl)
            .isPublic(isPublic)
            .ingredients(ingredients)
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

    public UserCommunity newInstanceWithManuals(List<UserCommunityManual> newManuals) {
        return UserCommunity.builder()
            .id(this.id)
            .user(this.user)
            .title(this.title)
            .content(this.content)
            .mainImageUrl(this.mainImageUrl)
            .isPublic(this.isPublic)
            .manuals(new ArrayList<>(newManuals))
            .ingredients(this.ingredients)
            .comments(this.comments)
            .likes(this.likes)
            .build();
    }

    public UserCommunity newInstanceWithIngredients(List<UserCommunityIngredient> newIngredients) {
        return UserCommunity.builder()
            .id(this.id)
            .user(this.user)
            .title(this.title)
            .content(this.content)
            .mainImageUrl(this.mainImageUrl)
            .isPublic(this.isPublic)
            .manuals(this.manuals)
            .ingredients(new ArrayList<>(newIngredients))
            .comments(this.comments)
            .likes(this.likes)
            .build();
    }
}
