package com.mars.app.domain.community.dto;

import jakarta.validation.constraints.Size;

public record CommunityRequestDto(
    @Size(max = 100, message = "게시물 제목은 최대 100자까지 입력할 수 있습니다.")
    String title,
    String content,
    String imageUrl,
    boolean isPublic
) {
}
