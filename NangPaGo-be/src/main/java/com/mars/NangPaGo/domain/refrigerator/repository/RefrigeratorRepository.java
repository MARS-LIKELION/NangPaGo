package com.mars.NangPaGo.domain.refrigerator.repository;

import com.mars.NangPaGo.domain.refrigerator.entity.Refrigerator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface RefrigeratorRepository extends JpaRepository<Refrigerator, Long> {
    @Query("SELECT r FROM Refrigerator r " +
            "JOIN FETCH r.ingredient i " +
            "WHERE r.user.email = :email")
    List<Refrigerator> findByUserEmail(@Param("email") String email);

    void deleteByUser_EmailAndIngredient_Name(@Param("email") String email, @Param("name") String name);
}
