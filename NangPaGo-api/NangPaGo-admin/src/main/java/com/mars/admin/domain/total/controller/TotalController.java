package com.mars.admin.domain.total.controller;

import com.mars.admin.domain.total.service.TotalService;
import com.mars.common.dto.ResponseDto;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/admin")
public class TotalController {

    private final TotalService totalService;

    @GetMapping("/dashboard")
    public ResponseDto<Map<String, Long>> dashboard() {
        return ResponseDto.of(totalService.getTotals(), "");
    }
}
