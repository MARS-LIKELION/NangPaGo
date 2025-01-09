package com.mars.NangPaGo.domain.comment.community.repository;

import com.mars.NangPaGo.domain.comment.community.entity.CommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommunityCommentRepository extends JpaRepository<CommunityComment, Long> {

    Page<CommunityComment> findByCommunityId(Long comunityId, Pageable pageable);

    int countByCommunityId(Long communityId);
}
