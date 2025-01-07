package com.mars.NangPaGo.domain.user.dto;

import lombok.Builder;
import org.springframework.boot.context.properties.bind.DefaultValue;

@Builder
public record UserInfoRequestDto(
    String nickName,
    boolean duplicateCheck
) {

}
