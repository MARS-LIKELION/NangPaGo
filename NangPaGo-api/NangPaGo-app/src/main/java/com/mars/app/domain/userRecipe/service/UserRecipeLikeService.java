package com.mars.app.domain.userRecipe.service;

import com.mars.app.domain.userRecipe.dto.UserRecipeLikeResponseDto;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.userRecipe.UserRecipeLike;
import com.mars.app.domain.userRecipe.repository.UserRecipeLikeRepository;
import com.mars.app.domain.userRecipe.repository.UserRecipeRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.mars.common.exception.NPGExceptionType.*;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserRecipeLikeService {

    private final UserRecipeLikeRepository userRecipeLikeRepository;
    private final UserRecipeRepository userRecipeRepository;
    private final UserRepository userRepository;

    public boolean isLiked(Long recipeId, Long userId) {
        return userRecipeLikeRepository.findByUserAndUserRecipe(
            validateUser(userId), validateUserRecipe(recipeId)
        ).isPresent();
    }

    public long getLikeCount(Long recipeId) {
        return userRecipeLikeRepository.countByUserRecipeId(recipeId);
    }

    @Transactional
    public UserRecipeLikeResponseDto toggleLike(Long recipeId, Long userId) {
        boolean isLikedAfterToggle = toggleLikeStatus(recipeId, userId);
        return UserRecipeLikeResponseDto.of(recipeId, isLikedAfterToggle);
    }

    private boolean toggleLikeStatus(Long recipeId, Long userId) {
        User user = validateUser(userId);
        UserRecipe userRecipe = validateUserRecipe(recipeId);

        return userRecipeLikeRepository.findByUserAndUserRecipe(user, userRecipe)
            .map(this::removeLike)
            .orElseGet(() -> addLike(user, userRecipe));
    }

    private boolean removeLike(UserRecipeLike userRecipeLike) {
        userRecipeLikeRepository.delete(userRecipeLike);
        return false;
    }

    private boolean addLike(User user, UserRecipe userRecipe) {
        userRecipeLikeRepository.save(UserRecipeLike.of(user, userRecipe));
        return true;
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
            .orElseThrow(NOT_FOUND_USER::of);
    }

    private UserRecipe validateUserRecipe(Long recipeId) {
        return userRecipeRepository.findById(recipeId)
            .orElseThrow(NOT_FOUND_RECIPE::of);
    }
}
