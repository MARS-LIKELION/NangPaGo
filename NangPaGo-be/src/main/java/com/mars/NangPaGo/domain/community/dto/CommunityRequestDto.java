package com.mars.NangPaGo.domain.community.dto;

import jakarta.validation.constraints.NotEmpty;

public record CommunityRequestDto(
    @NotEmpty(message = "게시물 제목은 비어 있을 수 없습니다.")
    String title
) {
}
