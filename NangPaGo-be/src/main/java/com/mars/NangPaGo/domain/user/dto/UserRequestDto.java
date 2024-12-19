package com.mars.NangPaGo.domain.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
// @EqualsAndHashCode
public class UserRequestDto {

    private String name;
    private String password;
    private String email;

    @Builder
    private UserRequestDto(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public static UserRequestDto create(String name) {
        return UserRequestDto.builder()
            .name(name)
            .build();
    }

    public static UserRequestDto create() {
        return UserRequestDto.builder()
            .name("기본값")
            .build();
    }

    public static UserRequestDto createWithPassword(String password) {
        return UserRequestDto.builder()
            .name("기본값")
            .password(password)
            .build();
    }
}
