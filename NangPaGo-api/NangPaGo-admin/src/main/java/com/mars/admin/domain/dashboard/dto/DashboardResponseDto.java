package com.mars.admin.domain.dashboard.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record DashboardResponseDto(
    Map<String, Long> totals,
    List<MonthRegisterCountDto> monthlyRegisterData,
    List<MonthPostCountDto> monthPostCountData,
    List<DailyUserStatsDto> dailyUserStats,
    List<HourlyUserActionCountDto> hourlyUserActionCountsDto
) {
    public static DashboardResponseDto of(
        Map<String, Long> totals,
        List<MonthRegisterCountDto> monthlyRegisterData,
        List<MonthPostCountDto> monthPostCountData,
        List<DailyUserStatsDto> dailyUserStats,
        List<HourlyUserActionCountDto> hourlyUserActionCountsData
    ) {
        return DashboardResponseDto.builder()
            .totals(totals)
            .monthlyRegisterData(monthlyRegisterData)
            .monthPostCountData(monthPostCountData)
            .dailyUserStats(dailyUserStats)
            .hourlyUserActionCountsDto(hourlyUserActionCountsData)
            .build();
    }
}
