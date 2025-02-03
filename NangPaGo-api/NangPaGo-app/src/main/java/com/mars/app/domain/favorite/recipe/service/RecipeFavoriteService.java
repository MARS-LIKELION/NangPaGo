package com.mars.app.domain.favorite.recipe.service;

import com.mars.app.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.common.dto.page.PageDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.exception.NPGExceptionType;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.app.domain.favorite.recipe.repository.RecipeFavoriteRepository;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class RecipeFavoriteService {

    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final UserRepository userRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeCommentRepository recipeCommentRepository;

    public boolean isFavorite(Long recipeId, Long userId) {
        return recipeFavoriteRepository.findByUserIdAndRecipeId(userId, recipeId).isPresent();
    }

    public PageDto<RecipeFavoriteListResponseDto> getFavoriteRecipes(Long userId, PageRequestVO pageRequestVO) {
        User user = userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);

        return PageDto.of(
            recipeFavoriteRepository.findAllByUser(user, pageRequestVO.toPageable())
                .map(favorite -> {
                    Recipe recipe = favorite.getRecipe();
                    int likeCount = recipeLikeRepository.countByRecipeId(recipe.getId());
                    int commentCount = recipeCommentRepository.countByRecipeId(recipe.getId());
                    return RecipeFavoriteListResponseDto.of(recipe, likeCount, commentCount);
                })
        );
    }

}
