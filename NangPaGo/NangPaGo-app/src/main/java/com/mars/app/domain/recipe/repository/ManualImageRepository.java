package com.mars.app.domain.recipe.repository;

import com.mars.common.model.recipe.ManualImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ManualImageRepository extends JpaRepository<ManualImage, Long> {
}
