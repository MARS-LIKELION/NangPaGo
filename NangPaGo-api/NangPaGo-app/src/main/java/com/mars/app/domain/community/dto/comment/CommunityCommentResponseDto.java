package com.mars.app.domain.community.dto.comment;

import com.mars.common.model.comment.community.CommunityComment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommunityCommentResponseDto(
    Long id,
    Long postId,
    String content,
    String writerName,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CommunityCommentResponseDto of(CommunityComment communityComment, Long userId) {
        return CommunityCommentResponseDto.builder()
            .id(communityComment.getId())
            .postId(communityComment.getCommunity().getId())
            .content(communityComment.getContent())
            .writerName(communityComment.getUser().getNickname())
            .isOwnedByUser(communityComment.getUser().getId().equals(userId))
            .createdAt(communityComment.getCreatedAt())
            .updatedAt(communityComment.getUpdatedAt())
            .build();
    }

    private static String maskEmail(String email) {
        if (email.indexOf("@") <= 3) {
            return email.replaceAll("(?<=.).(?=.*@)", "*");
        }
        return email.replaceAll("(?<=.{3}).(?=.*@)", "*");
    }
}
