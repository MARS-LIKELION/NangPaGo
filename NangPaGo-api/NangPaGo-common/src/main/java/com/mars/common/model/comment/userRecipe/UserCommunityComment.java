package com.mars.common.model.comment.userRecipe;

import com.mars.common.model.BaseEntity;
import com.mars.common.model.userCommunity.UserCommunity;
import com.mars.common.model.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class UserCommunityComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_community_id", nullable = false)
    private UserCommunity userCommunity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public static UserCommunityComment create(UserCommunity userCommunity, User user, String content) {
        return UserCommunityComment.builder()
            .userCommunity(userCommunity)
            .user(user)
            .content(content)
            .build();
    }

    public void updateText(String content) {
        this.content = content;
    }
}
