package com.mars.admin.domain.dashboard.dto;

import lombok.Builder;

@Builder
public record HourlyUserActionCountDto(
    String hour,
    String action,
    long count
) {
    public static HourlyUserActionCountDto of(int hour, String action, long count) {
        return HourlyUserActionCountDto.builder()
            .hour(String.format("%02d시", hour))
            .action(action)
            .count(count)
            .build();
    }
}
