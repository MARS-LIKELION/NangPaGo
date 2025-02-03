package com.mars.common.dto.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public record PageRequestVO(int pageNo, int pageSize) {
    private static final int DEFAULT_PAGE_NO = 1;
    private static final int DEFAULT_PAGE_SIZE = 12;
    private static final int MAX_PAGE_SIZE = 30;

    public PageRequestVO(int pageNo, int pageSize) {
        this.pageNo = validatePageNo(pageNo);
        this.pageSize = validatePageSize(pageSize);
    }

    public static PageRequestVO of(Integer pageNo, Integer pageSize) {
        return new PageRequestVO(
            Optional.ofNullable(pageNo).orElse(DEFAULT_PAGE_NO),
            Optional.ofNullable(pageSize).orElse(DEFAULT_PAGE_SIZE)
        );
    }

    public Pageable toPageable() {
        return PageRequest.of(pageNo - 1, pageSize);
    }

    private int validatePageNo(int pageNo) {
        return Math.max(pageNo, DEFAULT_PAGE_NO);
    }

    private int validatePageSize(int pageSize) {
        return Math.min(Math.max(pageSize, DEFAULT_PAGE_SIZE), MAX_PAGE_SIZE);
    }

}
