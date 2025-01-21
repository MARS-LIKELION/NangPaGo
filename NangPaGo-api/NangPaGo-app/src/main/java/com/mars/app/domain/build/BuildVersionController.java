package com.mars.app.domain.build;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@Tag(name = "공통 부분 API", description = "전 페이지에서 사용되는 공통 기능을 제공하는 API")
@RestController
@RequestMapping("/api/common")
public class BuildVersionController {

    private final BuildVersionProperties buildVersionProperties;

    @Operation(summary = "배포 버전 조회", description = "Footer를 통해 현재 애플리케이션의 배포 버전을 조회")
    @GetMapping("/version")
    public String getVersion() {
        return buildVersionProperties.getVersion();
    }
}
