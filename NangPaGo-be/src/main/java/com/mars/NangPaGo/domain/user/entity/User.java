package com.mars.NangPaGo.domain.user.entity;

import com.mars.NangPaGo.common.jpa.BaseEntity;
import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import com.mars.NangPaGo.domain.recipe.entity.RecipeLike;
import com.mars.NangPaGo.domain.user.enums.Gender;
import com.mars.NangPaGo.domain.user.enums.Provider;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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
    private Provider provider;
    private String providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeComment> comments;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecipeLike> likes;
}
