package com.mars.admin.domain.dashboard.controller;

import com.mars.admin.domain.dashboard.dto.DashboardResponseDto;
import com.mars.admin.domain.dashboard.dto.DashboardResponseDto.DashboardData;
import com.mars.admin.domain.dashboard.dto.MonthPostCountDto;
import com.mars.admin.domain.dashboard.service.ChartService;
import com.mars.common.dto.ResponseDto;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
@RestController
public class DashboardController {

    private final ChartService chartService;

    @GetMapping
    public ResponseDto<DashboardData> getDashboardData(@RequestParam(defaultValue = "11") int months) {
        Map<String, Long> totals = chartService.getTotals();
        List<DashboardResponseDto> monthlyData = chartService.getMonthlyRegisterCounts(months);
        List<MonthPostCountDto> monthPostCountData = chartService.getMonthPostCountTotals();

        DashboardData dashboardData = new DashboardData(totals, monthlyData, monthPostCountData);
        return ResponseDto.of(dashboardData, "");
    }
}
