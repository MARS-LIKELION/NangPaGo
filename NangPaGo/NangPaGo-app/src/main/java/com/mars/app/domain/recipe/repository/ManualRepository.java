package com.mars.app.domain.recipe.repository;

import com.mars.app.domain.recipe.entity.Manual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualRepository extends JpaRepository<Manual, Long> {
}
