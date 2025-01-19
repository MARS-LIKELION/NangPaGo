package com.mars.app.domain.user.entity;

import com.mars.app.auth.enums.OAuth2Provider;
import com.mars.app.common.jpa.BaseEntity;
import com.mars.app.domain.comment.recipe.entity.RecipeComment;
import com.mars.app.domain.favorite.recipe.entity.RecipeFavorite;
import com.mars.app.domain.recipe.entity.RecipeLike;
import com.mars.app.domain.user.dto.UserInfoRequestDto;
import com.mars.app.domain.user.enums.Gender;
import com.mars.app.domain.user.enums.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class User extends BaseEntity {

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
    @Column(name="user_status")
    private UserStatus userStatus;
    private LocalDate leftAt;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeLike> likes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeFavorite> favorites;

    public User updateNickname(UserInfoRequestDto requestDto) {
        this.nickname = requestDto.nickname();
        return this;
    }

    public void softDelete(){
        this.userStatus = UserStatus.from("탈퇴");
        this.leftAt = LocalDate.now();
    }
}
