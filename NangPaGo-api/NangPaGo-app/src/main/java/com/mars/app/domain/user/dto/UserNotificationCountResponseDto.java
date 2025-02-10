package com.mars.app.domain.user.dto;

import lombok.Builder;

@Builder
public record UserNotificationCountResponseDto(
    long count
) {
    public static UserNotificationCountResponseDto of(long count) {
        return UserNotificationCountResponseDto.builder()
            .count(count)
            .build();
    }
}
