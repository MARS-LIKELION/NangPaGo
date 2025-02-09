package com.mars.app.domain.community.service;

import com.mars.app.domain.community.repository.CommunityLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityLikeService {

    private final CommunityLikeRepository communityLikeRepository;

    public boolean isLiked(Long id, Long userId) {
        return communityLikeRepository.findByUserIdAndCommunityId(userId, id).isPresent();
    }

    public long getLikeCount(Long id) {
        return communityLikeRepository.countByCommunityId(id);
    }
}
