package com.mars.admin.domain.chart.service;

import com.mars.admin.domain.community.repository.CommunityRepository;
import com.mars.admin.domain.user.repository.UserRepository;
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

    private long getUserTotalCount(){
        return userRepository.count();
    }

    private long getCommunityTotalCount(){
        return communityRepository.count();
    }
}
