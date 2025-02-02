package com.mars.app.domain.comment.userCommunity.repository;

import com.mars.common.model.comment.userRecipe.UserCommunityComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserCommunityCommentRepository extends JpaRepository<UserCommunityComment, Long> {

    Page<UserCommunityComment> findByUserCommunityId(Long userCommunityId, Pageable pageable);

    long countByUserCommunityId(Long userCommunityId);
}
