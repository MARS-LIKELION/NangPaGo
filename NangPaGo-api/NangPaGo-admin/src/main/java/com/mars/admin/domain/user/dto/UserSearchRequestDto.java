package com.mars.admin.domain.user.dto;

import com.mars.admin.domain.user.enums.UserListSearchType;
import lombok.Builder;

@Builder
public record UserSearchRequestDto(
    UserListSearchType userListSearchType,
    String searchKeyword
) {
}
