package com.mars.NangPaGo.domain.comment.recipe.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_COMMENT;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import com.mars.NangPaGo.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class RecipeCommentService {

    private final RecipeCommentRepository recipeCommentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public PageDto<RecipeCommentResponseDto> pagedCommentsByRecipe(Long recipeId,
                                                                   String email,
                                                                   int pageNo,
                                                                   int pageSize) {
        validateRecipe(recipeId);
        return PageDto.of(
            recipeCommentRepository.findByRecipeId(recipeId, createPageRequest(pageNo, pageSize))
                .map(comment -> RecipeCommentResponseDto.from(comment, email))
        );
    }

    @Transactional
    public RecipeCommentResponseDto create(RecipeCommentRequestDto requestDto, String email, Long recipeId) {
        User user = findUserByEmail(email);
        return RecipeCommentResponseDto.from(recipeCommentRepository.save(
            RecipeComment.create(validateRecipe(recipeId), user, requestDto.content())), user.getEmail());
    }

    @Transactional
    public RecipeCommentResponseDto update(Long commentId, String email, RecipeCommentRequestDto requestDto) {
        RecipeComment comment = validateComment(commentId);
        validateOwnership(comment, email);
        comment.updateText(requestDto.content());
        return RecipeCommentResponseDto.from(comment, email);
    }

    @Transactional
    public void delete(Long commentId, String email) {
        RecipeComment comment = validateComment(commentId);
        validateOwnership(comment, email);
        recipeCommentRepository.delete(comment);
    }

    private void validateOwnership(RecipeComment comment, String email) {
        if (!comment.getUser().getEmail().equals(email)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("댓글을 수정/삭제할 권한이 없습니다.");
        }
    }

    private Recipe validateRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다."));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }

    private RecipeComment validateComment(Long commentId) {
        return recipeCommentRepository.findById(commentId)
            .orElseThrow(() -> NOT_FOUND_COMMENT.of("댓글을 찾을 수 없습니다."));
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }
}
