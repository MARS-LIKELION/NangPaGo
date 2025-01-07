package com.mars.NangPaGo.domain.user.dto;

import lombok.Builder;

@Builder
public record UserInfoRequestDto(
    String nickname
) {

}
