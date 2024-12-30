package com.mars.NangPaGo.domain.ingredient.repository;

import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByName(String name);
}
