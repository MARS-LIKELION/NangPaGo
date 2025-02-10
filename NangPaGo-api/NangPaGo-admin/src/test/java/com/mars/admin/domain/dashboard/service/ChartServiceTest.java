package com.mars.admin.domain.dashboard.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.dashboard.dto.MonthPostCountDto;
import com.mars.admin.domain.user.repository.UserRepository;
import com.mars.admin.domain.user.service.UserService;
import com.mars.admin.support.IntegrationTestSupport;
import com.mars.common.model.community.Community;
import com.mars.common.model.user.User;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class ChartServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChartService chartService;
    @Autowired
    private UserService userService;

    private User createUser() {
        return User.builder()
            .email("test@example.com")
            .build();
    }

    private Community createCommunity(User user) {
        return Community.of(user, "제목", "내용", "", true);
    }

    @DisplayName("이번 달 게시글 총합 수를 조회할 수 있다.")
    @Test
    void getMonthPostCountTotals() {
        // given
        User user = createUser();
        userRepository.save(user);

        List<Community> posts = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            posts.add(createCommunity(user));
        }
        communityRepository.saveAll(posts);

        int minMonth = 4;
        YearMonth currentMonthKey = YearMonth.now();
        String minMonthKey = currentMonthKey.minusMonths(minMonth - 1)
            .format(DateTimeFormatter.ofPattern("MM")) + "월";

        // when
        List<MonthPostCountDto> totals = chartService.getPostMonthCountTotals();

        // then
        assertThat(totals.size()).isEqualTo(minMonth);
        assertThat(totals.get(0).month()).contains(minMonthKey);
        assertThat(totals.get(3).count()).isEqualTo(5L);
    }
}
