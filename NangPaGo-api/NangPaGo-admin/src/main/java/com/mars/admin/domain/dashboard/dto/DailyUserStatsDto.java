package com.mars.admin.domain.dashboard.dto;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record DailyUserStatsDto(
    String date,
    long users,
    long unregisteredUsers
) {
    private static final DateTimeFormatter INPUT_FORMATTER = DateTimeFormatter.ofPattern("M/d");
    private static final DateTimeFormatter OUTPUT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static DailyUserStatsDto of(String dateString, long users, long unregisteredUsers) {
        LocalDate date = LocalDate.parse(dateString, INPUT_FORMATTER);
        return DailyUserStatsDto.builder()
            .date(date.format(OUTPUT_FORMATTER))
            .users(users)
            .unregisteredUsers(unregisteredUsers)
            .build();
    }
}
