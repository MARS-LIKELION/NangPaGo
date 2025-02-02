package com.mars.app.domain.comment.userCommunity.service;

import static com.mars.common.exception.NPGExceptionType.*;

import com.mars.app.domain.comment.userCommunity.dto.UserCommunityCommentRequestDto;
import com.mars.app.domain.comment.userCommunity.dto.UserCommunityCommentResponseDto;
import com.mars.common.dto.PageDto;
import com.mars.app.domain.comment.userCommunity.repository.UserCommunityCommentRepository;
import com.mars.common.model.comment.userRecipe.UserCommunityComment;
import com.mars.common.model.userCommunity.UserCommunity;
import com.mars.app.domain.userCommunity.repository.UserCommunityRepository;
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
public class UserCommunityCommentService {

    private final UserCommunityCommentRepository userCommunityCommentRepository;
    private final UserCommunityRepository userCommunityRepository;
    private final UserRepository userRepository;

    public PageDto<UserCommunityCommentResponseDto> pagedCommentsByUserCommunity(Long userCommunityId,
                                                                                 Long userId,
                                                                                 int pageNo,
                                                                                 int pageSize) {
        if (pageNo < 0) throw BAD_REQUEST_INVALID_PAGE_NO.of();
        if (pageSize <= 0) throw BAD_REQUEST_INVALID_PAGE_SIZE.of();

        validateUserCommunity(userCommunityId);
        return PageDto.of(
            userCommunityCommentRepository.findByUserCommunityId(userCommunityId, createPageRequest(pageNo, pageSize))
                .map(comment -> UserCommunityCommentResponseDto.of(comment, userId))
        );
    }

    @Transactional
    public UserCommunityCommentResponseDto create(UserCommunityCommentRequestDto requestDto, Long userId, Long userCommunityId) {
        if (requestDto.content() == null || requestDto.content().trim().isEmpty()) {
            throw BAD_REQUEST_INVALID_COMMENT.of();
        }

        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);

        return UserCommunityCommentResponseDto.of(
            userCommunityCommentRepository.save(
                UserCommunityComment.create(validateUserCommunity(userCommunityId), user, requestDto.content())
            ),
            user.getId()
        );
    }

    @Transactional
    public UserCommunityCommentResponseDto update(Long commentId, Long userId, UserCommunityCommentRequestDto requestDto) {
        if (requestDto.content() == null || requestDto.content().trim().isEmpty()) {
            throw BAD_REQUEST_INVALID_COMMENT.of();
        }

        UserCommunityComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        comment.updateText(requestDto.content());
        return UserCommunityCommentResponseDto.of(comment, userId);
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        UserCommunityComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        userCommunityCommentRepository.delete(comment);
    }

    private void validateOwnership(UserCommunityComment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("댓글을 수정/삭제할 권한이 없습니다.");
        }
    }

    private UserCommunity validateUserCommunity(Long userCommunityId) {
        return userCommunityRepository.findById(userCommunityId)
            .orElseThrow(NOT_FOUND_COMMUNITY::of);
    }

    private UserCommunityComment validateComment(Long commentId) {
        return userCommunityCommentRepository.findById(commentId)
            .orElseThrow(NOT_FOUND_COMMENT::of);
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
