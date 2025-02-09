package com.mars.app.domain.community.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY_COMMENT;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;

import com.mars.common.dto.page.PageResponseDto;
import com.mars.app.domain.community.dto.comment.CommunityCommentRequestDto;
import com.mars.app.domain.community.dto.comment.CommunityCommentResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.model.comment.community.CommunityComment;
import com.mars.app.domain.community.repository.CommunityCommentRepository;
import com.mars.common.model.community.Community;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public PageResponseDto<CommunityCommentResponseDto> pagedCommentsByCommunity(
        Long communityId,
        Long userId,
        PageRequestVO pageRequestVO
    ) {
        validateCommunity(communityId);
        return PageResponseDto.of(communityCommentRepository.findByCommunityId(communityId, pageRequestVO.toPageable())
                .map(comment -> CommunityCommentResponseDto.of(comment, userId))
        );
    }

    @Transactional
    public CommunityCommentResponseDto create(CommunityCommentRequestDto requestDto, Long userId, Long communityId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);

        return CommunityCommentResponseDto.of(communityCommentRepository.save(
            CommunityComment.create(validateCommunity(communityId), user, requestDto.content())), user.getId());
    }

    @Transactional
    public CommunityCommentResponseDto update(Long commentId, Long userId, CommunityCommentRequestDto requestDto) {
        CommunityComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        comment.updateText(requestDto.content());
        return CommunityCommentResponseDto.of(comment, userId);
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        CommunityComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        communityCommentRepository.delete(comment);
    }

    private void validateOwnership(CommunityComment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("댓글을 수정/삭제할 권한이 없습니다.");
        }
    }

    private Community validateCommunity(Long communityId) {
        return communityRepository.findById(communityId)
            .orElseThrow(NOT_FOUND_COMMUNITY::of);
    }

    private CommunityComment validateComment(Long commentId) {
        return communityCommentRepository.findById(commentId)
            .orElseThrow(NOT_FOUND_COMMUNITY_COMMENT::of);
    }

}
