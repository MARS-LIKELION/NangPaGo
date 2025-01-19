package com.mars.common.dto.user;

import com.mars.common.model.user.User;
import lombok.Builder;

@Builder
public record MyPageDto(
    String nickName,
    String providerName,
    int likeCount,
    int favoriteCount,
    int commentCount
) {

    public static MyPageDto of(User user, int likeCount, int favoriteCount, int commentCount) {
        return MyPageDto.builder()
            .nickName(user.getNickname())
            .providerName(user.getOauth2Provider().name())
            .likeCount(likeCount)
            .favoriteCount(favoriteCount)
            .commentCount(commentCount)
            .build();
    }
}
