package com.mars.app.domain.user.dto;

import lombok.Builder;

@Builder
public record UserInfoRequestDto(
    String nickname
) {

}
