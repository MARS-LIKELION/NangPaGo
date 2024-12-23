package com.mars.NangPaGo.domain.recipe.entity;

import com.mars.NangPaGo.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "recipe_manual")
@Entity
public class Manual extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String manual;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
