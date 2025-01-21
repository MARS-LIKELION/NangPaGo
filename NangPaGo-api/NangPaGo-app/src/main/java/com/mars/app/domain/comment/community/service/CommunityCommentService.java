package com.mars.app.domain.comment.community.service;

import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_COMMUNITY_COMMENT;
import static com.mars.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mars.common.dto.PageDto;
import com.mars.app.domain.comment.community.dto.CommunityCommentRequestDto;
import com.mars.app.domain.comment.community.dto.CommunityCommentResponseDto;
import com.mars.common.model.comment.community.CommunityComment;
import com.mars.app.domain.comment.community.repository.CommunityCommentRepository;
import com.mars.common.model.community.Community;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class CommunityCommentService {

    private final CommunityCommentRepository communityCommentRepository;
    private final CommunityRepository communityRepository;
    private final UserRepository userRepository;

    public PageDto<CommunityCommentResponseDto> pagedCommentsByCommunity(Long communityId,
        String email,
        int pageNo,
        int pageSize) {
        validateCommunity(communityId);
        return PageDto.of(
            communityCommentRepository.findByCommunityId(communityId, createPageRequest(pageNo, pageSize))
                .map(comment -> CommunityCommentResponseDto.of(comment, email))
        );
    }

    @Transactional
    public CommunityCommentResponseDto create(CommunityCommentRequestDto requestDto, String email, Long communityId) {
        User user = findUserByEmail(email);
        return CommunityCommentResponseDto.of(communityCommentRepository.save(
            CommunityComment.create(validateCommunity(communityId), user, requestDto.content())), user.getEmail());
    }

    @Transactional
    public CommunityCommentResponseDto update(Long commentId, String email, CommunityCommentRequestDto requestDto) {
        CommunityComment comment = validateComment(commentId);
        validateOwnership(comment, email);
        comment.updateText(requestDto.content());
        return CommunityCommentResponseDto.of(comment, email);
    }

    @Transactional
    public void delete(Long commentId, String email) {
        CommunityComment comment = validateComment(commentId);
        validateOwnership(comment, email);
        communityCommentRepository.delete(comment);
    }

    private void validateOwnership(CommunityComment comment, String email) {
        if (!comment.getUser().getEmail().equals(email)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("댓글을 수정/삭제할 권한이 없습니다.");
        }
    }

    private Community validateCommunity(Long communityId) {
        return communityRepository.findById(communityId)
            .orElseThrow(() -> NOT_FOUND_COMMUNITY.of());
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of());
    }

    private CommunityComment validateComment(Long commentId) {
        return communityCommentRepository.findById(commentId)
            .orElseThrow(() -> NOT_FOUND_COMMUNITY_COMMENT.of());
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }
}
