package com.mars.NangPaGo.domain.user.dto;

import lombok.Builder;

@Builder
public record MyPageSubQueryCountDto(
    long likesCount,
    long favoritesCount,
    long commentsCount,
    long refrigeratorCount
) {

}
