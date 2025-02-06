package com.mars.admin.domain.dashboard.service;

import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.dashboard.dto.DashboardResponseDto;
import com.mars.admin.domain.dashboard.dto.MonthCommunityCountDto;
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

    public List<DashboardResponseDto> getMonthlyRegisterCounts(int months) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);

        List<Object[]> monthlyRegisterCounts = userRepository.getMonthRegisterCount(startDate, endDate);
        List<DashboardResponseDto> userCountDto = new ArrayList<>();

        for (Object[] row : monthlyRegisterCounts) {
            int year = ((Number) row[0]).intValue();
            int month = ((Number) row[1]).intValue();
            long userCount = ((Number) row[2]).longValue();
            System.out.println(year + "-" + month + "-" + userCount);
            userCountDto.add(new DashboardResponseDto(year, month, userCount));
        }

        System.out.println(userCountDto);
        return userCountDto;
    }

    public List<MonthCommunityCountDto> getPostMonthCountTotals() {
        List<YearMonth> months = resetMonths();
        Map<YearMonth, Long> postCounts = getMonthPostCounts().stream()
            .collect(Collectors.toMap(
                result -> YearMonth.parse(((String) result[0]).substring(0, 7)),
                result -> (Long) result[1]
            ));

        List<MonthCommunityCountDto> result = new ArrayList<>();

        boolean foundPosts = false;
        for (YearMonth month : months) {
            long count = postCounts.getOrDefault(month, 0L);
            if (count > 0) {
                foundPosts = true;
            }
            if (foundPosts) {
                result.add(MonthCommunityCountDto.of(month, count));
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

    private long getUserTotalCount() {
        return userRepository.count();
    }

    private long getCommunityTotalCount() {
        return communityRepository.count();
    }
}
