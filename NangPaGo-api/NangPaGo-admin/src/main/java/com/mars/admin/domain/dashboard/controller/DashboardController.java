package com.mars.admin.domain.dashboard.controller;

import com.mars.admin.domain.dashboard.dto.DailyUserStatsDto;
import com.mars.admin.domain.dashboard.dto.DashboardResponseDto;
import com.mars.admin.domain.dashboard.dto.MonthPostCountDto;
import com.mars.admin.domain.dashboard.dto.MonthRegisterCountDto;
import com.mars.admin.domain.dashboard.service.ChartService;
import com.mars.common.dto.ResponseDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@RestController
public class DashboardController {

    private final ChartService chartService;

    @GetMapping
    public ResponseDto<DashboardResponseDto> getDashboardData() {
        Map<String, Long> totals = chartService.getTotals();
        List<MonthRegisterCountDto> monthlyRegisterData = chartService.getMonthlyRegisterCounts();
        List<MonthPostCountDto> monthPostCountData = chartService.getPostMonthCountTotals();
        List<DailyUserStatsDto> dailyUserCountData = chartService.getDailyUserCounts();
        DashboardResponseDto dashboardResponseDto = DashboardResponseDto.of(totals, monthlyRegisterData,
            monthPostCountData, dailyUserCountData);
        return ResponseDto.of(dashboardResponseDto);
    }
}
