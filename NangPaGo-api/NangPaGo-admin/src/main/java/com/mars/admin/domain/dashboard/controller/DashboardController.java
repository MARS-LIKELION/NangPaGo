package com.mars.admin.domain.dashboard.controller;

import com.mars.admin.domain.dashboard.service.ChartService;
import com.mars.common.dto.ResponseDto;
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
    public ResponseDto<Map<String, Long>> dashboard() {
        return ResponseDto.of(chartService.getTotals(), "");
    }
}
