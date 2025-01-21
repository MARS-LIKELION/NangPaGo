package com.mars.common.dto.user;

import lombok.Builder;

@Builder
public record UserInfoRequestDto(
    String nickname
) {

}
