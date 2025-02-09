package com.mars.app.domain.community.repository;

import com.mars.common.model.comment.community.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    @Query("SELECT cc FROM CommunityComment cc JOIN cc.community c WHERE c.id = :communityId ORDER BY cc.updatedAt DESC")
    Page<CommunityComment> findByCommunityId(@Param("communityId") Long communityId, Pageable pageable);

    int countByCommunityId(Long communityId);
}
