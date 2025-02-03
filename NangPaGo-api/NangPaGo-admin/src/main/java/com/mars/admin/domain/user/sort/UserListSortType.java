package com.mars.admin.domain.user.sort;

import lombok.Getter;
import org.springframework.data.domain.Sort;

@Getter
public enum UserListSortType {
    ID_ASC("id", Sort.Direction.ASC),
    ID_DESC("id", Sort.Direction.DESC),
    NICKNAME_ASC("nickname", Sort.Direction.ASC),
    NICKNAME_DESC("nickname", Sort.Direction.DESC);

    private final String field;
    private final Sort.Direction direction;

    UserListSortType(String field, Sort.Direction direction) {
        this.field = field;
        this.direction = direction;
    }

}
