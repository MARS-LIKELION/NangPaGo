package com.mars.common.dto.page;

import jakarta.validation.constraints.Min;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public record PageRequestVO(
    @Min(value = 1, message = "pageNo는 1 이상이어야 합니다.") int pageNo,
    @Min(value = 1, message = "pageSize는 1 이상이어야 합니다.") int pageSize
) {
    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 12;

    public static PageRequestVO of(Integer pageNo, Integer pageSize) {
        return new PageRequestVO(
            Optional.ofNullable(pageNo).orElse(DEFAULT_PAGE_NO),
            Optional.ofNullable(pageSize).orElse(DEFAULT_PAGE_SIZE)
        );
    }

    public Pageable toPageable() {
        return PageRequest.of(pageNo - 1, pageSize);
    }
}
