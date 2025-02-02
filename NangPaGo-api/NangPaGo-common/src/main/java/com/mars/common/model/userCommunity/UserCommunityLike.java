package com.mars.common.model.userCommunity;

import com.mars.common.model.user.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "user_community_id"})})
@Entity
public class UserCommunityLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_community_id", nullable = false)
    private UserCommunity userCommunity;

    public static UserCommunityLike of(User user, UserCommunity userCommunity) {
        return UserCommunityLike.builder()
            .user(user)
            .userCommunity(userCommunity)
            .build();
    }
}
