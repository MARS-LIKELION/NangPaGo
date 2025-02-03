package com.mars.app.domain.comment.userRecipe.service;

import com.mars.app.domain.comment.userRecipe.dto.UserRecipeCommentRequestDto;
import com.mars.app.domain.comment.userRecipe.dto.UserRecipeCommentResponseDto;
import com.mars.app.domain.comment.userRecipe.repository.UserRecipeCommentRepository;
import com.mars.app.domain.userRecipe.repository.UserRecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.dto.PageDto;
import com.mars.common.model.comment.userRecipe.UserRecipeComment;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mars.common.exception.NPGExceptionType.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserRecipeCommentService {

    private final UserRecipeCommentRepository userRecipeCommentRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRepository userRepository;

    public PageDto<UserRecipeCommentResponseDto> pagedCommentsByUserRecipe(Long userRecipeId,
                                                                           Long userId,
                                                                           int pageNo ,
                                                                           int pageSize) {
        if (pageNo < -1) throw BAD_REQUEST_INVALID_PAGE_NO.of();
        if (pageSize <= 0) throw BAD_REQUEST_INVALID_PAGE_SIZE.of();

        validateUserRecipe(userRecipeId);
        return PageDto.of(
            userRecipeCommentRepository.findByUserRecipeId(userRecipeId, createPageRequest(pageNo, pageSize))
                .map(comment -> UserRecipeCommentResponseDto.of(comment, userId))
        );
    }

    @Transactional
    public UserRecipeCommentResponseDto create(@Valid UserRecipeCommentRequestDto requestDto, Long userId, Long userRecipeId) {
        User user = userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);

        return UserRecipeCommentResponseDto.of(
            userRecipeCommentRepository.save(
                UserRecipeComment.create(validateUserRecipe(userRecipeId), user, requestDto.content())
            ),
            user.getId()
        );
    }

    @Transactional
    public UserRecipeCommentResponseDto update(@Valid UserRecipeCommentRequestDto requestDto, Long commentId, Long userId) {

        UserRecipeComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        comment.updateText(requestDto.content());
        return UserRecipeCommentResponseDto.of(comment, userId);
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        UserRecipeComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        userRecipeCommentRepository.delete(comment);
    }

    private void validateOwnership(UserRecipeComment comment, Long userId) {
        if (!comment.getUser().getId().equals(userId)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("댓글을 수정/삭제할 권한이 없습니다.");
        }
    }

    private UserRecipe validateUserRecipe(Long userRecipeId) {
        return userRecipeRepository.findById(userRecipeId)
            .orElseThrow(NOT_FOUND_RECIPE::of);
    }

    private UserRecipeComment validateComment(Long commentId) {
        return userRecipeCommentRepository.findById(commentId)
            .orElseThrow(NOT_FOUND_COMMENT::of);
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(Math.max(pageNo, -1) , pageSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
