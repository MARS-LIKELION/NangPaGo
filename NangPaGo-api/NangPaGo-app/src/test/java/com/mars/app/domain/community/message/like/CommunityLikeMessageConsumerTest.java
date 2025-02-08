package com.mars.app.domain.community.message.like;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mars.app.domain.community.dto.like.CommunityLikeMessageDto;
import com.mars.app.domain.community.repository.CommunityLikeRepository;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.exception.NPGException;
import com.mars.common.model.community.Community;
import com.mars.common.model.community.CommunityLike;
import com.mars.common.model.user.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

@Transactional
class CommunityLikeMessageConsumerTest extends IntegrationTestSupport {

    @Autowired
    private CommunityLikeRepository communityLikeRepository;
    @Autowired
    private CommunityRepository communityRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommunityLikeMessageConsumer communityLikeMessageConsumer;

    @DisplayName("커뮤니티 게시글 좋아요 메시지를 처리하고 좋아요를 추가할 수 있다.")
    @Test
    void processLikeMessageAdd() {
        // given
        User user = createUser("user@example.com");
        userRepository.save(user);
        Community community = createCommunity(user);
        communityRepository.save(community);

        CommunityLikeMessageDto communityLikeMessageDto = CommunityLikeMessageDto.of(community.getId(), user.getId());

        // when
        communityLikeMessageConsumer.processLikeMessage(communityLikeMessageDto);

        // then
        assertThat(communityLikeRepository.findByUserIdAndCommunityId(user.getId(), community.getId()))
            .isPresent();
        assertThat(communityLikeRepository.countByCommunityId(community.getId())).isEqualTo(1);
    }

    @DisplayName("커뮤니티 게시글 좋아요 메시지를 처리하고 좋아요를 취소할 수 있다.")
    @Test
    void processLikeMessageRemove() {
        // given
        User user = createUser("user@example.com");
        userRepository.save(user);

        Community community = createCommunity(user);
        communityRepository.save(community);

        CommunityLike communityLike = CommunityLike.of(user, community);
        communityLikeRepository.save(communityLike);

        CommunityLikeMessageDto communityLikeMessageDto = CommunityLikeMessageDto.of(community.getId(), user.getId());

        // when
        communityLikeMessageConsumer.processLikeMessage(communityLikeMessageDto);

        // then
        assertThat(communityLikeRepository.findByUserIdAndCommunityId(user.getId(), community.getId()))
            .isEmpty();
        assertThat(communityLikeRepository.countByCommunityId(community.getId())).isZero();
    }

    @DisplayName("존재하지 않는 사용자의 좋아요 메시지를 처리할 때 예외가 발생한다.")
    @Test
    void processLikeMessageWithInvalidUser() {
        // given
        User user = createUser("user@example.com");
        Community community = createCommunity(user);
        userRepository.save(user);
        communityRepository.save(community);

        CommunityLikeMessageDto messageDto = CommunityLikeMessageDto.of(community.getId(), 9999L);

        // when & then
        assertThatThrownBy(() -> communityLikeMessageConsumer.processLikeMessage(messageDto))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("존재하지 않는 커뮤니티 게시글의 좋아요 메시지를 처리할 때 예외가 발생한다.")
    @Test
    void processLikeMessageWithInvalidRecipe() {
        // given
        User user = createUser("user@example.com");
        userRepository.save(user);

        CommunityLikeMessageDto messageDto = CommunityLikeMessageDto.of(9999L, user.getId());

        // when & then
        assertThatThrownBy(() -> communityLikeMessageConsumer.processLikeMessage(messageDto))
            .isInstanceOf(NPGException.class)
            .hasMessage("게시물을 찾을 수 없습니다.");
    }

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
}
