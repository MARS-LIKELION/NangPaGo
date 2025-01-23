package com.mars.app.domain.comment.community.dto;

import com.mars.common.model.comment.community.CommunityComment;
import java.time.LocalDateTime;
import lombok.Builder;

@Builder
public record CommunityCommentResponseDto(
    Long id,
    Long postId,
    String content,
    String email,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CommunityCommentResponseDto of(CommunityComment communityComment, String email) {
        return CommunityCommentResponseDto.builder()
            .id(communityComment.getId())
            .postId(communityComment.getCommunity().getId())
            .content(communityComment.getContent())
            .email(maskEmail(communityComment.getUser().getEmail()))
            .isOwnedByUser(communityComment.getUser().getEmail().equals(email))
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
