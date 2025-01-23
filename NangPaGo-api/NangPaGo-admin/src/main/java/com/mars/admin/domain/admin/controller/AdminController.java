package com.mars.admin.domain.admin.controller;

import com.mars.admin.domain.admin.service.AdminService;
import com.mars.common.dto.ResponseDto;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public ResponseDto<Map<String, Long>> dashboard() {
        return ResponseDto.of(adminService.getTotals(), "");
    }
}
