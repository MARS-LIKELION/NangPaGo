package com.mars.common.model.recipe;

import com.mars.common.model.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;

@Getter
@Table(name = "recipe_image")
@Entity
public class ManualImage extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
