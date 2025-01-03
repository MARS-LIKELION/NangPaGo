package com.mars.NangPaGo.domain.user.dto;

import lombok.Builder;

@Builder
public record UserActivityStats(
    long likesCount,
    long favoritesCount,
    long commentsCount,
    long refrigeratorCount
) {

}
