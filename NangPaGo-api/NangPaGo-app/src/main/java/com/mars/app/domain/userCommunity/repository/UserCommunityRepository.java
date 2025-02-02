package com.mars.app.domain.userCommunity.repository;

import com.mars.common.model.userCommunity.UserCommunity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserCommunityRepository extends JpaRepository<UserCommunity, Long> {

    Page<UserCommunity> findByIsPublicTrueOrUserId(Long userId, Pageable pageable);
}
