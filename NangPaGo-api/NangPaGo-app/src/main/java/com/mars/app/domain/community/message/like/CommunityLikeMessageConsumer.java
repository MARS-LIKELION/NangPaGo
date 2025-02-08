package com.mars.app.domain.community.message.like;

import com.mars.app.domain.community.dto.like.CommunityLikeMessageDto;
import com.mars.app.domain.community.event.CommunityLikeEvent;
import com.mars.app.domain.community.repository.CommunityLikeRepository;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.exception.NPGExceptionType;
import com.mars.common.model.community.Community;
import com.mars.common.model.community.CommunityLike;
import com.mars.common.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class CommunityLikeMessageConsumer {
    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    private final ApplicationEventPublisher sseEventPublisher;

    @Transactional
    @RabbitListener(queues = "#{@communityLikeQueue.name}")
    public void processLikeMessage(CommunityLikeMessageDto communityLikeMessageDto) {
        toggleLikeStatus(communityLikeMessageDto.communityId(), communityLikeMessageDto.userId());
        publishCommunityLikeEvent(communityLikeMessageDto);
    }

    private void toggleLikeStatus(Long id, Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);
        Community community = communityRepository.findById(id)
            .orElseThrow(() -> NPGExceptionType.NOT_FOUND_COMMUNITY.of("게시물을 찾을 수 없습니다."));

        communityLikeRepository.findByUserAndCommunity(user, community)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, community));
    }

    private boolean removeLike(CommunityLike communityLike) {
        communityLikeRepository.delete(communityLike);
        return false;
    }

    private boolean addLike(User user, Community community) {
        communityLikeRepository.save(CommunityLike.of(user, community));
        return true;
    }

    private void publishCommunityLikeEvent(CommunityLikeMessageDto communityLikeMessageDto) {
        int likeCount = getLikeCount(communityLikeMessageDto.communityId());
        sseEventPublisher.publishEvent(
            CommunityLikeEvent.of(
                this,
                communityLikeMessageDto.communityId(),
                communityLikeMessageDto.userId(),
                likeCount
            )
        );
    }

    private int getLikeCount(Long communityId) {
        return communityLikeRepository.countByCommunityId(communityId);
    }
}
