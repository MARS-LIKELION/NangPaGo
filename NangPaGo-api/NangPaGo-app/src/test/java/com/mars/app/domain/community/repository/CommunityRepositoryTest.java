package com.mars.app.domain.community.repository;

import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mars.app.support.AbstractRepositoryTestSupport;
import com.mars.common.model.community.Community;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

class CommunityRepositoryTest extends AbstractRepositoryTestSupport {

    @Autowired
    private CommunityRepository communityRepository;

    @DisplayName("findByIsPublicTrueOrUserId 테스트")
    @Test
    void findByIsPublicTrueOrUserIdTest() {
        // given
        Long userId = 6L;
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by(DESC, "createdAt"));
        // when
        Page<Community> byIsPublicTrueOrUserId = communityRepository.findByIsPublicTrueOrUserId(userId, pageRequest);

        // then
        if (!byIsPublicTrueOrUserId.isEmpty()) {
            for (Community community : byIsPublicTrueOrUserId) {
                System.out.println("Community title: " + community.getTitle()
                    + " / userId: " + community.getUser().getId()
                    + " / isPublic: " + community.isPublic());
            }
        }
    }
}
