package com.mars.NangPaGo.domain.recipe.entity;

import com.mars.NangPaGo.common.jpa.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Table(name = "recipe_manual")
public class Manual extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String manual;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
