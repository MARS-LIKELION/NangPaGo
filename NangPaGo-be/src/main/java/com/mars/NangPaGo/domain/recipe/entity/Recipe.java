package com.mars.NangPaGo.domain.recipe.entity;

import jakarta.persistence.*;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Recp_No;

    private String Recp_Title;

    private String Recp_Info;

    private String Recp_ImgUrl;

    private String Recp_Ingredients;

    private String Recp_Category;





}
