package com.mars.app.domain.comment.recipe.service;

import static com.mars.app.common.exception.NPGExceptionType.NOT_FOUND_COMMENT;
import static com.mars.app.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.app.common.exception.NPGExceptionType.NOT_FOUND_USER;
import static com.mars.app.common.exception.NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT;
import static org.springframework.data.domain.Sort.Direction.DESC;

import com.mars.app.common.dto.PageDto;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.app.domain.comment.recipe.entity.RecipeComment;
import com.mars.app.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.recipe.entity.Recipe;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.domain.user.entity.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class RecipeCommentService {

    private final RecipeCommentRepository recipeCommentRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;

    public PageDto<RecipeCommentResponseDto> pagedCommentsByRecipe(Long recipeId, String email, int pageNo, int pageSize) {
        Recipe recipe = findRecipeById(recipeId);
        return PageDto.of(
            recipeCommentRepository.findByRecipeId(recipeId, createPageRequest(pageNo, pageSize))
                .map(comment -> RecipeCommentResponseDto.from(comment, recipe, email))
        );
    }

    @Transactional
    public RecipeCommentResponseDto create(RecipeCommentRequestDto requestDto, String email, Long recipeId) {
        User user = findUserByEmail(email);
        Recipe recipe = findRecipeById(recipeId);
        RecipeComment newComment = RecipeComment.create(recipe, user, requestDto.content());
        RecipeComment savedComment = recipeCommentRepository.save(newComment);
        return RecipeCommentResponseDto.from(savedComment, recipe, user.getEmail());
    }

    @Transactional
    public RecipeCommentResponseDto update(Long commentId, String email, RecipeCommentRequestDto requestDto) {
        RecipeComment comment = findCommentById(commentId);
        validateOwnership(comment, email);
        comment.updateText(requestDto.content());
        return RecipeCommentResponseDto.from(comment, comment.getRecipe(), email);
    }

    @Transactional
    public void delete(Long commentId, String email) {
        RecipeComment comment = findCommentById(commentId);
        validateOwnership(comment, email);
        recipeCommentRepository.delete(comment);
    }

    public int countCommentsByRecipe(Long recipeId) {
        findRecipeById(recipeId);
        return recipeCommentRepository.countByRecipeId(recipeId);
    }

    private void validateOwnership(RecipeComment comment, String email) {
        if (!comment.getUser().getEmail().equals(email)) {
            throw UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT.of("댓글을 수정/삭제할 권한이 없습니다.");
        }
    }

    private Recipe findRecipeById(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다."));
    }

    private RecipeComment findCommentById(Long commentId) {
        return recipeCommentRepository.findById(commentId)
            .orElseThrow(() -> NOT_FOUND_COMMENT.of("댓글을 찾을 수 없습니다."));
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }
}
