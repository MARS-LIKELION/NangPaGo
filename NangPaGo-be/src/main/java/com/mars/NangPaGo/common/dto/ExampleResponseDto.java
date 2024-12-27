package com.mars.NangPaGo.common.dto;

import lombok.Builder;
import lombok.Getter;

// 이 Dto 클래스는 단순한 예제코드를 위해 작성한 것으로, 조만간 삭제할 예정입니다.
// TODO: 이 파일 제거 예정
@Getter
public class ExampleResponseDto {
    private final String name;
    private final String email;

    @Builder
    private ExampleResponseDto(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public static ExampleResponseDto of(String name, String email) {
        return ExampleResponseDto.builder()
            .name(name)
            .email(email)
            .build();
    }
}
