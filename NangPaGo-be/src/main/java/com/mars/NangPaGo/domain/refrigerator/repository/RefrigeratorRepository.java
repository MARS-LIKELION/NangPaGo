package com.mars.NangPaGo.domain.refrigerator.repository;

import com.mars.NangPaGo.domain.ingredient.entity.Ingredient;
import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import com.mars.NangPaGo.domain.user.entity.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {

    @Query("SELECT r FROM Refrigerator r " + "JOIN FETCH r.ingredient i " + "WHERE r.user.email = :email")
    List<Refrigerator> findByUserEmail(@Param("email") String email);

    boolean existsByUserAndIngredient(User user, Ingredient ingredient);

    @Modifying
    @Query("DELETE FROM Refrigerator r WHERE r.user.email = :email AND r.ingredient.name = :ingredientName")
    void deleteByUserEmailAndIngredientName(@Param("email") String email, @Param("ingredientName") String ingredientName);

    int countByUser(User user);
}
