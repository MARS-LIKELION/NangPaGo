package com.mars.NangPaGo.domain.community.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;

import com.mars.NangPaGo.domain.community.dto.CommunityLikeResponseDto;
import com.mars.NangPaGo.domain.community.entity.Community;
import com.mars.NangPaGo.domain.community.entity.CommunityLike;
import com.mars.NangPaGo.domain.community.repository.CommunityLikeRepository;
import com.mars.NangPaGo.domain.community.repository.CommunityRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityLikeService {

    private final CommunityLikeRepository communityLikeRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public boolean isLiked(Long id, String email) {
        return communityLikeRepository.findByEmailAndCommunityId(email, id).isPresent();
    }

    public long getLikeCount(Long id) {
        return communityLikeRepository.countByCommunityId(id);
    }

    @Transactional
    public CommunityLikeResponseDto toggleLike(Long id, String email) {
        boolean isLikedAfterToggle = toggleLikeStatus(id, email);
        return CommunityLikeResponseDto.of(id, isLikedAfterToggle);
    }

    @Transactional
    private boolean toggleLikeStatus(Long id, String email) {
        User user = getUserByEmail(email);
        Community community = communityRepository.findById(id)
            .orElseThrow(() -> NOT_FOUND_COMMUNITY.of("게시물을 찾을 수 없습니다."));
        return communityLikeRepository.findByUserAndCommunity(user, community)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, community));
    }

    private User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    private boolean removeLike(CommunityLike communityLike) {
        communityLikeRepository.delete(communityLike);
        return false;
    }

    @Transactional
    private boolean addLike(User user, Community community) {
        communityLikeRepository.save(CommunityLike.of(user, community));
        return true;
    }
}
