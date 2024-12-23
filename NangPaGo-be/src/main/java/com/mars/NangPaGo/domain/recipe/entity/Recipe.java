package com.mars.NangPaGo.domain.recipe.entity;

import com.mars.NangPaGo.common.jpa.BaseEntity;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Recipe extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "rcp_nm", nullable = false)
    private String name;

    @Column(name = "rcp_parts_dtls", columnDefinition = "TEXT")
    private String ingredients;

    @Column(name = "rcp_way2")
    private String cookingMethod;

    @Column(name = "rcp_pat2")
    private String category;

    @Column(name = "info_eng")
    private Integer calories;

    @Column(name = "info_fat")
    private Integer fat;

    @Column(name = "info_car")
    private Integer carbohydrates;

    @Column(name = "info_pro")
    private Integer protein;

    @Column(name = "info_na")
    private Integer sodium;

    @Column(name = "hash_tag")
    private String hashTag;

    @Column(name = "att_file_no_main")
    private String mainImage;

    @Column(name = "att_file_no_mk")
    private String stepImage;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "recipe")
    private List<Manual> manuals;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "recipe")
    private List<ManualImage> manualImages;
}
