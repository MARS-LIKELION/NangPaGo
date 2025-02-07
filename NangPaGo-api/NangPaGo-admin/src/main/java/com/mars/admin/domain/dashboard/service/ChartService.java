package com.mars.admin.domain.dashboard.service;

import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.dashboard.dto.MonthPostCountDto;
import com.mars.admin.domain.dashboard.dto.MonthRegisterCountDto;
import com.mars.admin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ChartService {

    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public Map<String, Long> getTotals() {
        return Map.of(
            "userCount", userRepository.count(),
            "communityCount", communityRepository.count()
        );
    }

    public List<MonthRegisterCountDto> getMonthlyRegisterCounts(int months) {
        YearMonth now = YearMonth.now();
        Map<YearMonth, Long> map = monthRegisterToMap(months);
        List<MonthRegisterCountDto> monthPostCountDtos = new ArrayList<>();

        YearMonth startDate = map.keySet().stream().findFirst().orElse(null);

        if (startDate == null) {
            return monthPostCountDtos;
        }

        for (YearMonth month = startDate; !month.isAfter(now); month = month.plusMonths(1)) {
            monthPostCountDtos.add(MonthRegisterCountDto.of(month, map.getOrDefault(month, 0L)));
        }

        return monthPostCountDtos;
    }

    public List<MonthPostCountDto> getPostMonthCountTotals(int months) {
        YearMonth now = YearMonth.now();
        Map<YearMonth, Long> map = monthPostCountToMap(months);
        List<MonthPostCountDto> monthPostCountDtos = new ArrayList<>();

        YearMonth startDate = map.keySet().stream().findFirst().orElse(null);

        if (startDate == null) {
            return monthPostCountDtos;
        }

        for (YearMonth month = startDate; !month.isAfter(now); month = month.plusMonths(1)) {
            monthPostCountDtos.add(MonthPostCountDto.of(month, map.getOrDefault(month, 0L)));
        }

        return monthPostCountDtos;
    }

    private Map<YearMonth, Long> monthPostCountToMap(int months) {
        return getMonthPostCounts(months).stream()
            .collect(Collectors.toMap(
                result -> YearMonth.parse(((String) result[0]).substring(0, 7)),
                result -> (Long) result[1]
            ));
    }

    private List<Object[]> getMonthPostCounts(int months) {
        YearMonth now = YearMonth.now();
        YearMonth startMonth = now.minusMonths(months);

        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = now.atEndOfMonth().atTime(23, 59, 59);

        return communityRepository.getMonthPostCounts(start, end);
    }

    private Map<YearMonth, Long> monthRegisterToMap(int months) {
        return getMonthRegisterCounts(months).stream()
            .collect(Collectors.toMap(
                result -> YearMonth.of(((Number) result[0]).intValue(), ((Number) result[1]).intValue()),
                result -> ((Number) result[2]).longValue()
            ));
    }

    private List<Object[]> getMonthRegisterCounts(int months) {
        YearMonth now = YearMonth.now();
        YearMonth startMonth = now.minusMonths(months);

        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = now.atEndOfMonth().atTime(23, 59, 59);

        return userRepository.getMonthRegisterCount(start, end);
    }
}
