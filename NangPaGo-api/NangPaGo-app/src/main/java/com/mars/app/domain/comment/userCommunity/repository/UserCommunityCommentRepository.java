package com.mars.app.domain.comment.userCommunity.repository;


import com.mars.common.model.comment.userRecipe.UserCommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommunityCommentRepository extends JpaRepository<UserCommunityComment, Long> {

    /**
     * 특정 커뮤니티 게시글의 댓글 목록 조회 (페이징)
     */
    Page<UserCommunityComment> findByUserCommunityId(Long userCommunityId, Pageable pageable);

    /**
     * 특정 커뮤니티 게시글의 댓글 개수 조회
     */
    long countByUserCommunityId(Long userCommunityId);
}
