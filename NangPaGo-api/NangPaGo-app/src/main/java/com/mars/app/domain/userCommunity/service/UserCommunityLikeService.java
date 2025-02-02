package com.mars.app.domain.userCommunity.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;
import com.mars.app.domain.userCommunity.dto.UserCommunityLikeResponseDto;
import com.mars.common.model.userCommunity.UserCommunity;
import com.mars.common.model.userCommunity.UserCommunityLike;
import com.mars.app.domain.userCommunity.repository.UserCommunityLikeRepository;
import com.mars.app.domain.userCommunity.repository.UserCommunityRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserCommunityLikeService {

    private final UserCommunityLikeRepository userCommunityLikeRepository;
    private final UserCommunityRepository userCommunityRepository;
    private final UserRepository userRepository;

    public boolean isLiked(Long postId, Long userId) {
        return userCommunityLikeRepository.findByUserAndUserCommunity(
            validateUser(userId), validateUserCommunity(postId)
        ).isPresent();
    }

    public long getLikeCount(Long postId) {
        return userCommunityLikeRepository.countByUserCommunityId(postId);
    }

    @Transactional
    public UserCommunityLikeResponseDto toggleLike(Long postId, Long userId) {
        boolean isLikedAfterToggle = toggleLikeStatus(postId, userId);
        return UserCommunityLikeResponseDto.of(postId, isLikedAfterToggle);
    }

    private boolean toggleLikeStatus(Long postId, Long userId) {
        User user = validateUser(userId);
        UserCommunity userCommunity = validateUserCommunity(postId);

        return userCommunityLikeRepository.findByUserAndUserCommunity(user, userCommunity)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, userCommunity));
    }

    private boolean removeLike(UserCommunityLike userCommunityLike) {
        userCommunityLikeRepository.delete(userCommunityLike);
        return false;
    }

    private boolean addLike(User user, UserCommunity userCommunity) {
        userCommunityLikeRepository.save(UserCommunityLike.of(user, userCommunity));
        return true;
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);
    }

    private UserCommunity validateUserCommunity(Long postId) {
        return userCommunityRepository.findById(postId)
            .orElseThrow(NOT_FOUND_COMMUNITY::of);
    }
}
