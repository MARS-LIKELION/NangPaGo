package com.mars.common.model.user;

import com.mars.common.dto.user.UserInfoRequestDto;
import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.enums.user.Gender;
import com.mars.common.enums.user.UserStatus;
import com.mars.common.model.BaseEntity;
import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.common.model.favorite.recipe.RecipeFavorite;
import com.mars.common.model.recipe.RecipeLike;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {

    public static final Long ANONYMOUS_USER_ID = -1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String nickname;
    private String birthday;

    @Enumerated(EnumType.STRING)
    private Gender gender;
    private String email;
    private String phone;
    private String profileImageUrl;
    private String role;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private OAuth2Provider oauth2Provider;
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(name = "user_status")
    private UserStatus userStatus;
    private LocalDate leftAt;

    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeLike> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeFavorite> favorites;

    public void updateNickname(UserInfoRequestDto requestDto) {
        this.nickname = requestDto.nickname();
    }

    public void updateUserStatus(UserStatus userStatus) {
        this.userStatus = userStatus;
    }

    public void softDelete() {
        this.userStatus = UserStatus.from("탈퇴");
        this.leftAt = LocalDate.now();
    }
}
