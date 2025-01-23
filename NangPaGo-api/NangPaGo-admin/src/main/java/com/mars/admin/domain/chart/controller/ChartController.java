package com.mars.admin.domain.chart.controller;

import com.mars.admin.domain.chart.service.ChartService;
import com.mars.common.dto.ResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class ChartController {

    private final ChartService chartService;

    @GetMapping("/dashboard")
    public ResponseDto<Map<String, Long>> dashboard() {
        return ResponseDto.of(chartService.getTotals(), "");
    }
}
