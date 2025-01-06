package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import lombok.Builder;

@Builder
public record MyPageDto(
    String nickName,
    String providerName,
    int likeCount,
    int favoriteCount,
    int commentCount,
    int refrigeratorCount
) {

    public static MyPageDto of(User user, int likeCount, int favoriteCount, int commentCount, int refrigeratorCount) {
        return MyPageDto.builder()
            .nickName(user.getNickname())
            .providerName(user.getOauth2Provider().name())
            .likeCount(likeCount)
            .favoriteCount(favoriteCount)
            .commentCount(commentCount)
            .refrigeratorCount(refrigeratorCount)
            .build();
    }
}
