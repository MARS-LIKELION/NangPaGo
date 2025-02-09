package com.mars.app.domain.user_recipe.service;

import com.mars.app.domain.user_recipe.dto.comment.UserRecipeCommentRequestDto;
import com.mars.app.domain.user_recipe.dto.comment.UserRecipeCommentResponseDto;
import com.mars.app.domain.user_recipe.repository.UserRecipeCommentRepository;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.model.comment.userRecipe.UserRecipeComment;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    public PageResponseDto<UserRecipeCommentResponseDto> pagedCommentsByUserRecipe(
        Long userRecipeId,
        Long userId,
        PageRequestVO pageRequestVO
    ) {
        validateUserRecipe(userRecipeId);
        return PageResponseDto.of(
            userRecipeCommentRepository.findByUserRecipeId(userRecipeId, pageRequestVO.toPageable())
                .map(comment -> UserRecipeCommentResponseDto.of(comment, userId))
        );
    }

    @Transactional
    public UserRecipeCommentResponseDto create(
        @Valid UserRecipeCommentRequestDto requestDto,
        Long userId,
        Long userRecipeId
    ) {
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
    public UserRecipeCommentResponseDto update(
        @Valid UserRecipeCommentRequestDto requestDto,
        Long commentId,
        Long userId
    ) {
        UserRecipeComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        comment.updateText(requestDto.content());
        return UserRecipeCommentResponseDto.of(comment, userId);
    }

    @Transactional
    public void delete(Long commentId, Long userId) {
        UserRecipeComment comment = validateComment(commentId);
        validateOwnership(comment, userId);
        comment.softDelete();
        userRecipeCommentRepository.save(comment);
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
}
