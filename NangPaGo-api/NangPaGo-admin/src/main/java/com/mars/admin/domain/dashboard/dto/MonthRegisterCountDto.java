package com.mars.admin.domain.dashboard.dto;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record MonthRegisterCountDto(
    String year,
    String month,
    long userCount
) {
    public static MonthRegisterCountDto of(YearMonth yearMonth, long userCount) {
        return MonthRegisterCountDto.builder()
            .year(yearMonth.format(DateTimeFormatter.ofPattern("yyyy")) + "년")
            .month(yearMonth.format(DateTimeFormatter.ofPattern("MM")) + "월")
            .userCount(userCount)
            .build();
    }
}
