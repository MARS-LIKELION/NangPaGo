package com.mars.app.domain.community.repository;

import com.mars.common.model.community.Community;
import com.mars.common.model.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityRepository extends JpaRepository<Community, Long> {

    @Query("SELECT c FROM Community c WHERE c.isPublic = true OR c.user.id = :userId ORDER BY c.updatedAt DESC")
    Page<Community> findByIsPublicTrueOrUserId(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT c FROM Community c WHERE c.user.id = :userId ORDER BY c.createdAt DESC")
    Page<Community> findByUserId(@Param("userId") Long userId, Pageable pageable);

    int countByUser(User user);
}
