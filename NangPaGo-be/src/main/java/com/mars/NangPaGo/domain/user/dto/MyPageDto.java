package com.mars.NangPaGo.domain.user.dto;

import com.mars.NangPaGo.domain.user.entity.User;
import lombok.Builder;

@Builder
public record MyPageDto(
    String nickName,
    String providerName,
    MyPageSubQueryCountDto myPageSubQueryCountDto
) {

    public static MyPageDto of(User user, MyPageSubQueryCountDto myPageSubQueryCountDto) {
        return MyPageDto.builder()
            .nickName(user.getNickname())
            .providerName(user.getProvider().name())
            .myPageSubQueryCountDto(myPageSubQueryCountDto)
            .build();
    }
}
