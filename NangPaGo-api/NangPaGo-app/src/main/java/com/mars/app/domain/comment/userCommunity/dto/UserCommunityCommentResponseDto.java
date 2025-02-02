package com.mars.app.domain.comment.userCommunity.dto;


import java.time.LocalDateTime;

import com.mars.common.model.comment.userRecipe.UserCommunityComment;
import lombok.Builder;

@Builder
public record UserCommunityCommentResponseDto(
    Long id,
    Long postId,
    String content,
    String writerName,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserCommunityCommentResponseDto of(UserCommunityComment userCommunityComment, Long userId) {
        return UserCommunityCommentResponseDto.builder()
            .id(userCommunityComment.getId())
            .postId(userCommunityComment.getUserCommunity().getId())
            .content(userCommunityComment.getContent())
            .writerName(userCommunityComment.getUser().getNickname())
            .isOwnedByUser(userCommunityComment.getUser().getId().equals(userId))
            .createdAt(userCommunityComment.getCreatedAt())
            .updatedAt(userCommunityComment.getUpdatedAt())
            .build();
    }
}
