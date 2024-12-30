package com.mars.NangPaGo.domain.comment.recipe.service;

import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_COMMENT;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_RECIPE;
import static com.mars.NangPaGo.common.exception.NPGExceptionType.NOT_FOUND_USER;
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
    public PageDto<RecipeCommentResponseDto> PagedCommentsByRecipe(Long recipeId, int pageNo, int pageSize) {
        vaildateRecipe(recipeId);
        return PageDto.of(recipeCommentRepository.findByRecipeId(recipeId, createPageRequest(pageNo, pageSize))
            .map(RecipeCommentResponseDto::from));
    }

    @Transactional
    public RecipeCommentResponseDto create(RecipeCommentRequestDto requestDto, Long recipeId) {
        return RecipeCommentResponseDto.from(recipeCommentRepository.save(
            RecipeComment.create(vaildateRecipe(recipeId), vaildateEmail(requestDto.userEmail()), requestDto.content())));
    }

    @Transactional
    public RecipeCommentResponseDto update(Long commentId, RecipeCommentRequestDto requestDto) {
        RecipeComment comment = vaildateComment(commentId);
        comment.updateText(requestDto.content());
        return RecipeCommentResponseDto.from(comment);
    }

    @Transactional
    public void delete(Long commentId) {
        recipeCommentRepository.delete(vaildateComment(commentId));
    }

    private PageRequest createPageRequest(int pageNo, int pageSize) {
        return PageRequest.of(pageNo, pageSize, Sort.by(DESC, "createdAt"));
    }

    private Recipe vaildateRecipe(Long recipeId) {
        return recipeRepository.findById(recipeId)
            .orElseThrow(() -> NOT_FOUND_RECIPE.of("레시피를 찾을 수 없습니다."));
    }

    private User vaildateEmail(String userEmail) {
        return userRepository.findByEmail(userEmail)
            .orElseThrow(() -> NOT_FOUND_USER.of("사용자를 찾을 수 없습니다."));
    }

    private RecipeComment vaildateComment(Long commentId) {
        return recipeCommentRepository.findById(commentId)
            .orElseThrow(() -> NOT_FOUND_COMMENT.of("댓글을 찾을 수 없습니다."));
    }
}
