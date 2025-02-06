package com.mars.admin.domain.dashboard.dto;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import lombok.Builder;

@Builder
public record MonthCommunityCountDto(
    String month,
    long count
) {

    public static MonthCommunityCountDto of(YearMonth yearMonth, long count) {
        return MonthCommunityCountDto.builder()
            .month(yearMonth.format(DateTimeFormatter.ofPattern("MM")) + "월")
            .count(count)
            .build();
    }
}
