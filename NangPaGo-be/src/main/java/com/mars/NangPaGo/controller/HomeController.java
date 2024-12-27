package com.mars.NangPaGo.controller;

import com.mars.NangPaGo.common.dto.ExampleResponseDto;
import com.mars.NangPaGo.common.dto.ResponseDto;
import com.mars.NangPaGo.common.exception.NPGExceptionType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    // TODO: 엔드포인트 삭제 (단순 예제를 위해 만듦)
    @GetMapping("/common/example/ok")
    public ResponseDto<ExampleResponseDto> exampleResponseDtoOk() {
        ExampleResponseDto exampleResponseDto = ExampleResponseDto.of("공통 DTO", "common@example.com");

        return ResponseDto.of(exampleResponseDto, "Message는 필요할때만 입력해도 됨");
    }

    // TODO: 엔드포인트 삭제 (단순 예제를 위해 만듦)
    @GetMapping("/common/example/exception/400")
    public ResponseDto<ExampleResponseDto> exampleResponseDtoException400() {
        throw NPGExceptionType.BAD_REQUEST.of("BAD_REQUEST 예외 발생 예시");
    }

    // TODO: 엔드포인트 삭제 (단순 예제를 위해 만듦)
    @GetMapping("/common/example/exception/500")
    public ResponseDto<ExampleResponseDto> exampleResponseDtoException500() {
        throw NPGExceptionType.SERVER_ERROR.of("INTERNAL_SERVER_ERROR 예외 발생 예시");
    }
}
