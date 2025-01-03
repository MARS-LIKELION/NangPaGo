package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import lombok.Builder;

@Builder
public record MyPageDto(
    String nickName,
    String providerName,
    UserActivityStats userActivityStats
) {

    public static MyPageDto of(User user, UserActivityStats userActivityStats) {
        return MyPageDto.builder()
            .nickName(user.getNickname())
            .providerName(user.getProvider().name())
            .userActivityStats(userActivityStats)
            .build();
    }
}
