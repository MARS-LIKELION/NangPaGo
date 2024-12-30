package com.mars.NangPaGo.domain.refrigerator.entity;

import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import com.mars.NangPaGo.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"user_id", "ingredient_id"})}) 
@Entity
public class Refrigerator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id")
    private Ingredient ingredient;

    @Builder
    private Refrigerator(User user, Ingredient ingredient) {
        this.user = user;
        this.ingredient = ingredient;
    }

    public static Refrigerator of(User user, Ingredient ingredient) {
        return Refrigerator.builder()
                .user(user)
                .ingredient(ingredient)
                .build();
    }
}
