package com.mars.admin.domain.dashboard.service;

import com.mars.admin.domain.audit.repository.AuditLogRepository;
import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.dashboard.dto.DailyUserStatsDto;
import com.mars.admin.domain.dashboard.dto.HourlyUserActionCountDto;
import com.mars.admin.domain.dashboard.dto.MonthPostCountDto;
import com.mars.admin.domain.dashboard.dto.MonthRegisterCountDto;
import com.mars.admin.domain.stats.repository.VisitLogRepository;
import com.mars.admin.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
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
    private final VisitLogRepository visitLogRepository;
    private final AuditLogRepository auditLogRepository;

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

    public List<DailyUserStatsDto> getDailyUserCounts() {
        return visitLogRepository.getDailyUserStats();
    }

    public List<HourlyUserActionCountDto> getHourlyActionCounts() {
        Date oneMonthAgoDate = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);

        List<HourlyUserActionCountDto> results = auditLogRepository.findHourlyActionCounts(oneMonthAgoDate);

        Map<Integer, Map<String, Long>> groupedCounts = IntStream.range(0, 24)
            .boxed()
            .collect(Collectors.toMap(
                hour -> hour,
                hour -> new HashMap<>()
            ));

        for (HourlyUserActionCountDto dto : results) {
            int hour = Integer.parseInt(dto.hour().replace("시", ""));
            String groupedAction = getGroupedAction(dto.action());

            groupedCounts
                .computeIfAbsent(hour, k -> new HashMap<>())
                .merge(groupedAction, dto.count(), Long::sum);
        }

        List<HourlyUserActionCountDto> hourlyCounts = new ArrayList<>();
        for (int hour = 0; hour < 24; hour++) {
            for (String group : List.of("회원 활동", "유저 레시피", "커뮤니티", "레시피")) {
                hourlyCounts.add(HourlyUserActionCountDto.of(
                    hour, group, groupedCounts.get(hour).getOrDefault(group, 0L)
                ));
            }
        }

        return hourlyCounts;
    }

    private String getGroupedAction(String action) {
        if (action.startsWith("USER_RECIPE")) {
            return "유저 레시피";
        }
        else if (action.startsWith("USER")) {
            return "회원 활동";
        }
        else if (action.startsWith("RECIPE")) {
            return "레시피";
        }
        else if (action.startsWith("COMMUNITY")) {
            return "커뮤니티";
        }
        return action;
    }
}
