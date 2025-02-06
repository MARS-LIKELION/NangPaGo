package com.mars.admin.domain.dashboard.service;

import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.dashboard.dto.MonthPostCountDto;
import com.mars.admin.domain.dashboard.dto.MonthRegisterCountDto;
import com.mars.admin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.time.YearMonth;
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
        List<YearMonth> monthRange = resetMonths(months);
        Map<YearMonth, Long> registerCounts = getMonthRegisterCounts().stream()
            .collect(Collectors.toMap(
                row -> YearMonth.of(((Number) row[0]).intValue(), ((Number) row[1]).intValue()),
                row -> ((Number) row[2]).longValue()
            ));

        List<MonthRegisterCountDto> result = new ArrayList<>();

        boolean foundRegisters = false;
        for (YearMonth month : monthRange) {
            long count = registerCounts.getOrDefault(month, 0L);
            if (count > 0) {
                foundRegisters = true;
            }
            if (foundRegisters) {
                result.add(MonthRegisterCountDto.of(month, count));
            }
        }

        return result;
    }

    private List<YearMonth> resetMonths(int months) {
        YearMonth now = YearMonth.now();
        List<YearMonth> monthRange = new ArrayList<>();
        for (YearMonth month = now.minusMonths(months - 1); !month.isAfter(now); month = month.plusMonths(1)) {
            monthRange.add(month);
        }
        return monthRange;
    }

    private List<Object[]> getMonthRegisterCounts() {
        YearMonth now = YearMonth.now();
        YearMonth startMonth = now.minusMonths(11);

        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = now.atEndOfMonth().atTime(23, 59, 59);

        return userRepository.getMonthRegisterCount(start, end);
    }


    public List<MonthPostCountDto> getPostMonthCountTotals() {
        List<YearMonth> monthRange = resetMonths();
        Map<YearMonth, Long> postCounts = getMonthPostCounts().stream()
            .collect(Collectors.toMap(
                result -> YearMonth.parse(((String) result[0]).substring(0, 7)),
                result -> (Long) result[1]
            ));

        List<MonthPostCountDto> result = new ArrayList<>();
        boolean foundPosts = false;
        for (YearMonth month : monthRange) {
            long count = postCounts.getOrDefault(month, 0L);
            if (count > 0) {
                foundPosts = true;
            }
            if (foundPosts) {
                result.add(MonthPostCountDto.of(month, count));
            }
        }

        return result;
    }

    private List<YearMonth> resetMonths() {
        YearMonth now = YearMonth.now();
        List<YearMonth> months = new ArrayList<>();
        for (YearMonth month = now.minusMonths(11); !month.isAfter(now); month = month.plusMonths(1)) {
            months.add(month);
        }
        return months;
    }

    private List<Object[]> getMonthPostCounts() {
        YearMonth now = YearMonth.now();
        YearMonth startMonth = now.minusMonths(11);

        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = now.atEndOfMonth().atTime(23, 59, 59);

        return communityRepository.getMonthPostCounts(start, end);
    }
}
