package com.mars.admin.domain.dashboard.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Getter;

@Builder
public record DashboardResponseDto(Integer year, Integer month, Long userCount) {

    @Getter
    @Builder
    public static class DashboardData {
        private Map<String, Long> totals;
        private List<DashboardResponseDto> monthlyData;
        private List<MonthPostCountDto> monthPostCountData;

        public DashboardData(Map<String, Long> totals, List<DashboardResponseDto> monthlyData,
            List<MonthPostCountDto> monthPostCountData) {
            this.totals = totals;
            this.monthlyData = monthlyData;
            this.monthPostCountData = monthPostCountData;
        }
    }
}
