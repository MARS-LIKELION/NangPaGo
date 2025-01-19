package com.mars.app.domain.community.repository;

import com.mars.common.model.community.Community;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    Page<Community> findByIsPublicTrue(Pageable pageable);
    Page<Community> findByIsPublicTrueOrUserEmail(String email, Pageable pageable);
}
