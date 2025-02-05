package com.mars.admin.domain.dashboard.service;

import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    public Map<String, Long> getPostMonthCountTotals() {
        Map<String, Long> monthlyPostCounts = new LinkedHashMap<>();
        resetMap(monthlyPostCounts);

        getMonthPostCounts().forEach(result -> {
            String monthStr = ((String) result[0]).substring(5, 7) + "월";
            Long count = (Long) result[1];
            monthlyPostCounts.put(monthStr, count);
        });

        return monthlyPostCounts;
    }

    private void resetMap(Map<String, Long> map) {
        for (int i = 11; i >= 0; i--) {
            String monthKey = YearMonth.now().minusMonths(i).format(DateTimeFormatter.ofPattern("MM")) + "월";
            map.put(monthKey, 0L);
        }
    }

    private List<Object[]> getMonthPostCounts() {
        YearMonth now = YearMonth.now();
        YearMonth startMonth = now.minusMonths(11);

        LocalDateTime start = startMonth.atDay(1).atStartOfDay();
        LocalDateTime end = now.atEndOfMonth().atTime(23, 59, 59);

        return communityRepository.getMonthPostCounts(start, end);
    }

    private long getUserTotalCount() {
        return userRepository.count();
    }

    private long getCommunityTotalCount() {
        return communityRepository.count();
    }
}
