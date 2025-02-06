package com.mars.admin.domain.dashboard.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;

@Builder
public record DashboardResponseDto(
    Map<String, Long> totals,
    List<MonthRegisterCountDto> monthlyRegisterData,
    List<MonthPostCountDto> monthPostCountData
) {
    public static DashboardResponseDto of(
        Map<String, Long> totals,
        List<MonthRegisterCountDto> monthlyRegisterData,
        List<MonthPostCountDto> monthPostCountData
    ) {
        return DashboardResponseDto.builder()
            .totals(totals)
            .monthlyRegisterData(monthlyRegisterData)
            .monthPostCountData(monthPostCountData)
            .build();
    }
}
