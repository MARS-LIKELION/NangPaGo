package com.mars.NangPaGo.domain.community.repository;

import static jakarta.persistence.LockModeType.PESSIMISTIC_WRITE;

import com.mars.NangPaGo.domain.community.entity.Community;
import com.mars.NangPaGo.domain.community.entity.CommunityLike;
import com.mars.NangPaGo.domain.user.entity.User;
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

    @Query("SELECT COUNT(clike) FROM CommunityLike clike WHERE clike.community.id = :communityId")
    long countByCommunityId(@Param("communityId") Long communityId);

    int countByUser(User user);
}
