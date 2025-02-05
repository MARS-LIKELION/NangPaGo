package com.mars.admin.domain.community.repository;

import com.mars.common.model.community.Community;
import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommunityRepository extends JpaRepository<Community, Long> {
    long countByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
