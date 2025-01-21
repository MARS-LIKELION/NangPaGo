package com.mars.app.domain.community.repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.mars.common.model.community.Community;
import com.mars.common.model.community.CommunityLike;
import com.mars.common.model.user.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CommunityLikeRepository extends JpaRepository<CommunityLike, Long> {

    @Lock(PESSIMISTIC_WRITE)
    Optional<CommunityLike> findByUserAndCommunity(User user, Community community);

    @Query("SELECT clike FROM CommunityLike clike WHERE clike.user.email = :email AND clike.community.id = :communityId")
    Optional<CommunityLike> findByEmailAndCommunityId(@Param("email") String email, @Param("communityId") Long communityId);

    int countByCommunityId(Long communityId);
}
