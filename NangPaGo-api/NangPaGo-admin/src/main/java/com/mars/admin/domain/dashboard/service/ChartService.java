package com.mars.admin.domain.dashboard.service;

import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
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

    public Map<String, Long> getTotals(){
        return Map.of(
            "userCount", userRepository.count(),
            "communityCount", communityRepository.count()
        );
    }

    public Map<String, Long> getPostMonthCountTotals(){
        YearMonth now = YearMonth.now();

        Map<String, Long> monthlyPostCounts = new LinkedHashMap<>();

        for (int i = 11; i >= 0; i--) {
            YearMonth yearMonth = now.minusMonths(i);
            String monthKey = yearMonth.format(DateTimeFormatter.ofPattern("MM")) + "월";
            long postCount = getMonthPostCount(yearMonth);
            monthlyPostCounts.put(monthKey, postCount);
        }

        return monthlyPostCounts;
    }

    private long getMonthPostCount(YearMonth yearMonth){
        LocalDateTime start = yearMonth.atDay(1).atStartOfDay();
        LocalDateTime end = yearMonth.atEndOfMonth().atTime(23, 59, 59);
        return communityRepository.countByCreatedAtBetween(start, end);
    }

    private long getUserTotalCount(){
        return userRepository.count();
    }

    private long getCommunityTotalCount(){
        return communityRepository.count();
    }
}
