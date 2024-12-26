package com.mars.NangPaGo.controller;

import com.mars.NangPaGo.common.dto.ExampleResponseDto;
import com.mars.NangPaGo.common.dto.ResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/nangpago")
    public String nangPaGo() {
        return "냉파고의 시작";
    }

    // TODO: 이 엔드포인트 삭제 (단순 예제를 위해 만듦)
    @GetMapping("/common/example")
    public ResponseDto<ExampleResponseDto> exampleResponseDto() {
        ExampleResponseDto exampleResponseDto = ExampleResponseDto.of("공통 DTO", "common@example.com");

        return ResponseDto.of(exampleResponseDto, "Message는 필요할때만 입력해도 됨");
    }
}
