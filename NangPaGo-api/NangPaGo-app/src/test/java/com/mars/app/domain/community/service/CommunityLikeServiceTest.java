package com.mars.app.domain.community.service;

import com.mars.app.domain.community.repository.CommunityLikeRepository;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.model.community.Community;
import com.mars.common.model.community.CommunityLike;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import com.mars.common.model.user.User;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.assertThat;

@Transactional
class CommunityLikeServiceTest extends IntegrationTestSupport {

    @Autowired
    private CommunityLikeRepository communityLikeRepository;

    @Autowired
    private CommunityRepository communityRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityLikeService communityLikeService;

    private User createUser(String email) {
        return User.builder()
            .email(email)
            .build();
    }

    private Community createCommunity(User user) {
        return Community.builder()
            .title("Test Community")
            .content("Test Content")
            .isPublic(true)
            .user(user)
            .build();
    }

    @DisplayName("사용자는 게시물에 좋아요를 추가할 수 있다.")
    @Test
    void addLike() {
        // given
        User user = createUser("user@example.com");
        userRepository.save(user);

        Community community = createCommunity(user);
        communityRepository.save(community);

        // when: 처음 좋아요를 누름
        var response = communityLikeService.toggleLike(community.getId(), user.getId());

        // then: 좋아요 상태가 true로 변경됨
        assertThat(response.liked()).isTrue();
    }

    @DisplayName("사용자는 게시물의 좋아요를 취소할 수 있다.")
    @Test
    void cancelLike() {
        // given
        User user = createUser("user@example.com");
        userRepository.save(user);

        Community community = createCommunity(user);
        communityRepository.save(community);
        
        CommunityLike communityLike = CommunityLike.of(user, community);
        communityLikeRepository.save(communityLike);

        // when : 좋아요를 다시 누름
        var response = communityLikeService.toggleLike(community.getId(), user.getId());

        // then : 좋아요 상태가 false로 변경됨
        assertThat(response.liked()).isFalse();
    }


    @DisplayName("좋아요 상태를 확인할 때, 좋아요가 눌린 상태라면 true를 반환한다.")
    @Test
    void isLiked_statusCheck() {
        // given
        User user = createUser("user@example.com");
        userRepository.save(user);

        Community community = createCommunity(user);
        communityRepository.save(community);

        // 좋아요를 직접 CommunityLike 엔티티로 추가
        CommunityLike like = CommunityLike.of(user, community);
        communityLikeRepository.save(like);

        // when
        boolean liked = communityLikeService.isLiked(community.getId(), user.getId());

        // then
        assertThat(liked).isTrue();
    }


    @DisplayName("사용자는 좋아요 개수를 확인할 수 있다.")
    @Test
    void getLikeCount() {
        // given
        User user1 = createUser("user1@example.com");
        User user2 = createUser("user2@example.com");
        userRepository.saveAll(List.of(user1, user2));

        Community community = createCommunity(user1);
        communityRepository.save(community);

        // 좋아요 추가
        CommunityLike like1 = CommunityLike.of(user1, community);
        CommunityLike like2 = CommunityLike.of(user2, community);
        communityLikeRepository.saveAll(List.of(like1, like2));

        // when
        Long likeCount = communityLikeService.getLikeCount(community.getId());

        // then
        assertThat(likeCount).isEqualTo(2);
    }
}
