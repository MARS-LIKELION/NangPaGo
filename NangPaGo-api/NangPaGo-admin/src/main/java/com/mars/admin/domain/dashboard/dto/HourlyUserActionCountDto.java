package com.mars.admin.domain.dashboard.dto;

import lombok.Builder;

@Builder
public record HourlyUserActionCountDto(
    String hour,
    long count
) {
    public static HourlyUserActionCountDto of(int hour, long count) {
        return HourlyUserActionCountDto.builder()
            .hour(String.format("%02d시", hour))
            .count(count)
            .build();
    }
}
