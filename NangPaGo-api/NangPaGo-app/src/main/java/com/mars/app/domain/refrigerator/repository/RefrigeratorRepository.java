package com.mars.app.domain.refrigerator.repository;

import com.mars.common.model.ingredient.Ingredient;
import com.mars.common.model.refrigerator.Refrigerator;
import com.mars.common.model.user.User;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {

    @Query("SELECT r FROM Refrigerator r " + "JOIN FETCH r.ingredient i " + "WHERE r.user.id = :userId")
    List<Refrigerator> findByUserId(@Param("userId") Long userId);

    boolean existsByUserAndIngredient(User user, Ingredient ingredient);

    @Modifying
    @Query("DELETE FROM Refrigerator r WHERE r.user.id = :userId AND r.ingredient.name = :ingredientName")
    void deleteByUserIdAndIngredientName(@Param("userId") Long userId, @Param("ingredientName") String ingredientName);

    int countByUser(User user);
}
