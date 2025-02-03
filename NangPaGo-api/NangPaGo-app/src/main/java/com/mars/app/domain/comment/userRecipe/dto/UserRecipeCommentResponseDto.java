package com.mars.app.domain.comment.userRecipe.dto;

import java.time.LocalDateTime;
import com.mars.common.model.comment.userRecipe.UserRecipeComment;
import lombok.Builder;

@Builder
public record UserRecipeCommentResponseDto(
    Long id,
    Long postId,
    String content,
    String writerName,
    boolean isOwnedByUser,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static UserRecipeCommentResponseDto of(UserRecipeComment userRecipeComment, Long userId) {
        return UserRecipeCommentResponseDto.builder()
            .id(userRecipeComment.getId())
            .postId(userRecipeComment.getUserRecipe().getId())
            .content(userRecipeComment.getContent())
            .writerName(userRecipeComment.getUser().getNickname())
            .isOwnedByUser(userRecipeComment.getUser().getId().equals(userId))
            .createdAt(userRecipeComment.getCreatedAt())
            .updatedAt(userRecipeComment.getUpdatedAt())
            .build();
    }
}
