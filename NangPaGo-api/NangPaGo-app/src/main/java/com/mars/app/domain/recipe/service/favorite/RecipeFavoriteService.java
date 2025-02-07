package com.mars.app.domain.recipe.service.favorite;

import com.mars.app.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.recipe.repository.like.RecipeLikeRepository;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.exception.NPGExceptionType;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteListResponseDto;
import com.mars.app.domain.recipe.repository.favorite.RecipeFavoriteRepository;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
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

    public PageResponseDto<RecipeFavoriteListResponseDto> getFavoriteRecipes(Long userId, PageRequestVO pageRequestVO) {
        User user = userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);
        List<RecipeLike> recipeLikes = getRecipeLikesBy(userId);

        return PageResponseDto.of(
            recipeFavoriteRepository.findAllByUser(user, pageRequestVO.toPageable())
                .map(favorite -> {
                    Recipe recipe = favorite.getRecipe();
                    int likeCount = recipeLikeRepository.countByRecipeId(recipe.getId());
                    int commentCount = recipeCommentRepository.countByRecipeId(recipe.getId());
                    return RecipeFavoriteListResponseDto.of(recipe, likeCount, commentCount, recipeLikes);
                })
        );
    }

    private List<RecipeLike> getRecipeLikesBy(Long userId) {
        return userId.equals(User.ANONYMOUS_USER_ID)
            ? new ArrayList<>()
            : recipeLikeRepository.findRecipeLikesByUserId(userId);
    }
}
