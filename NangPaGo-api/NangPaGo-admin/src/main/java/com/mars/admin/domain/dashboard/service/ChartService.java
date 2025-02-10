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

    private static int MAX_MONTHS = 12;
    private static int MIN_MONTHS = 4;
    
    private final UserRepository userRepository;
    private final CommunityRepository communityRepository;

    public Map<String, Long> getTotals() {
        return Map.of(
            "userCount", userRepository.count(),
            "communityCount", communityRepository.count()
        );
    }

    public List<MonthRegisterCountDto> getMonthlyRegisterCounts() {
        YearMonth now = YearMonth.now();
        Map<YearMonth, Long> map = monthRegisterToMap();
        List<MonthRegisterCountDto> monthRegisterCountDtos = new ArrayList<>();

        YearMonth startDate = map.keySet().stream().findFirst().orElse(null);
        YearMonth fourMonthsAgo = YearMonth.from(now).minusMonths(MIN_MONTHS - 1);

        startDate = startDate.isAfter(fourMonthsAgo) ? fourMonthsAgo : startDate;

        for (YearMonth month = startDate; !month.isAfter(now); month = month.plusMonths(1)) {
            monthRegisterCountDtos.add(MonthRegisterCountDto.of(month, map.getOrDefault(month, 0L)));
        }

        return monthRegisterCountDtos;
    }

    public List<MonthPostCountDto> getPostMonthCountTotals() {
        YearMonth now = YearMonth.now();
        Map<YearMonth, Long> map = monthPostCountToMap();
        List<MonthPostCountDto> monthPostCountDtos = new ArrayList<>();

        YearMonth startDate = map.keySet().stream().findFirst().orElse(null);
        YearMonth fourMonthsAgo = YearMonth.from(now).minusMonths(MIN_MONTHS - 1);

        startDate = startDate.isAfter(fourMonthsAgo) ? fourMonthsAgo : startDate;

        for (YearMonth month = startDate; !month.isAfter(now); month = month.plusMonths(1)) {
            monthPostCountDtos.add(MonthPostCountDto.of(month, map.getOrDefault(month, 0L)));
        }

        return monthPostCountDtos;
    }

    private Map<YearMonth, Long> monthPostCountToMap() {
        return getMonthPostCounts().stream()
            .collect(Collectors.toMap(
                result -> YearMonth.parse(((String) result[0]).substring(0, 7)),
                result -> (Long) result[1]
            ));
    }

    private List<Object[]> getMonthPostCounts() {
        YearMonth now = YearMonth.now();
        YearMonth startMonth = now.minusMonths(MAX_MONTHS - 1);

        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = now.atEndOfMonth().atTime(23, 59, 59);

        return communityRepository.getMonthPostCounts(start, end);
    }

    private Map<YearMonth, Long> monthRegisterToMap() {
        return getMonthRegisterCounts().stream()
            .collect(Collectors.toMap(
                result -> YearMonth.of(((Number) result[0]).intValue(), ((Number) result[1]).intValue()),
                result -> ((Number) result[2]).longValue()
            ));
    }

    private List<Object[]> getMonthRegisterCounts() {
        YearMonth now = YearMonth.now();
        YearMonth startMonth = now.minusMonths(MAX_MONTHS - 1);

        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = now.atEndOfMonth().atTime(23, 59, 59);

        return userRepository.getMonthRegisterCount(start, end);
    }
}
