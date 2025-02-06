package com.mars.admin.domain.dashboard.dto;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record MonthPostCountDto(
    String month,
    long count
) {

    public static MonthPostCountDto of(YearMonth yearMonth, long count) {
        return MonthPostCountDto.builder()
            .month(yearMonth.format(DateTimeFormatter.ofPattern("MM")) + "월")
            .count(count)
            .build();
    }
}
