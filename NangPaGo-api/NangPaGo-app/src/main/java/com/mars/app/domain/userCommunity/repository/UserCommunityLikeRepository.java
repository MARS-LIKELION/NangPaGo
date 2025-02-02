package com.mars.app.domain.userCommunity.repository;

import com.mars.common.model.userCommunity.UserCommunityLike;
import com.mars.common.model.userCommunity.UserCommunity;
import com.mars.common.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserCommunityLikeRepository extends JpaRepository<UserCommunityLike, Long> {

    long countByUserCommunityId(Long userCommunityId);
    Optional<UserCommunityLike> findByUserAndUserCommunity(User user, UserCommunity userCommunity);
}
