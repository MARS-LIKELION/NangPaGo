package com.mars.app.domain.user.service;

import com.mars.app.domain.community.dto.CommunityResponseDto;
import com.mars.app.domain.community.repository.CommunityRepository;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.exception.NPGExceptionType;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.app.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteListResponseDto;
import com.mars.app.domain.recipe.repository.favorite.RecipeFavoriteRepository;
import com.mars.app.domain.recipe.dto.RecipeResponseDto;
import com.mars.app.domain.recipe.repository.like.RecipeLikeRepository;
import com.mars.app.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.common.dto.user.MyPageDto;
import com.mars.common.dto.user.UserInfoRequestDto;
import com.mars.common.dto.user.UserInfoResponseDto;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    public static final int MIN_NICKNAME_LENGTH = 1;
    public static final int MAX_NICKNAME_LENGTH = 20;

    private final UserRepository userRepository;
    private final RecipeLikeRepository recipeLikeRepository;
    private final RecipeFavoriteRepository recipeFavoriteRepository;
    private final RecipeCommentRepository recipeCommentRepository;
    private final CommunityRepository communityRepository;
    private final RefrigeratorRepository refrigeratorRepository;

    public UserResponseDto getCurrentUser(Long userId) {
        return UserResponseDto.from(userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of));
    }

    public MyPageDto getMyPage(Long userId) {
        User user = findUserById(userId);

        int likeCount = recipeLikeRepository.countByUser(user);
        int favoriteCount = recipeFavoriteRepository.countByUser(user);
        int postCount = communityRepository.countByUser(user);
        int commentCount = recipeCommentRepository.countByUser(user);

        return MyPageDto.of(user, likeCount, favoriteCount, postCount, commentCount);
    }

    public PageResponseDto<RecipeResponseDto> getMyLikedRecipes(Long userId, PageRequestVO pageRequestVO) {
        return PageResponseDto.of(
            recipeLikeRepository.findRecipeLikeByUser(findUserById(userId), pageRequestVO.toPageable())
                .map(recipeLike -> RecipeResponseDto.from(recipeLike.getRecipe()))
        );
    }

    public PageResponseDto<RecipeFavoriteListResponseDto> getMyFavorites(Long userId, PageRequestVO pageRequestVO) {
        return PageResponseDto.of(recipeFavoriteRepository.findAllByUser(findUserById(userId), pageRequestVO.toPageable())
                .map(recipeFavorite -> {
                    Recipe recipe = recipeFavorite.getRecipe();
                    int likeCount = recipeLikeRepository.countByRecipeId(recipe.getId());
                    int commentCount = recipeCommentRepository.countByRecipeId(recipe.getId());
                    return RecipeFavoriteListResponseDto.of(recipe, likeCount, commentCount);
                })
        );
    }

    public PageResponseDto<RecipeCommentResponseDto> getMyComments(Long userId, PageRequestVO pageRequestVO) {
        return PageResponseDto.of(recipeCommentRepository.findByUserIdWithRecipe(userId, pageRequestVO.toPageable())
                .map(recipeComment -> {
                    return RecipeCommentResponseDto.from(recipeComment, recipeComment.getRecipe(), userId);
                })
        );
    }

    public PageResponseDto<CommunityResponseDto> getMyPosts(Long userId, PageRequestVO pageRequestVO) {
        return PageResponseDto.of(communityRepository.findByUserId(userId, pageRequestVO.toPageable())
            .map(community -> CommunityResponseDto.of(community, userId)));
    }

    public UserInfoResponseDto getUserDetailInfo(Long userId) {
        return UserInfoResponseDto.from(findUserById(userId));
    }

    public boolean isNicknameAvailable(String targetNickname) {
        validateNickname(targetNickname);
        return true;
    }

    @Transactional
    public UserInfoResponseDto updateUserInfo(UserInfoRequestDto requestDto, Long userId) {
        String targetNickname = requestDto.nickname().trim();
        validateNickname(targetNickname);

        User updatedUser = updateNickname(requestDto, userId);
        return UserInfoResponseDto.from(updatedUser);
    }

    private void validateNickname(String targetNickname) {
        if (isDuplicatedNickname(targetNickname)) {
            throw NPGExceptionType.BAD_REQUEST_UNUSABLE_NICKNAME.of("이미 사용중인 닉네임입니다.");
        }

        if (targetNickname.length() <= MIN_NICKNAME_LENGTH) {
            throw NPGExceptionType.BAD_REQUEST_UNUSABLE_NICKNAME.of("두글자 이상 입력해주세요.");
        }

        if (targetNickname.length() > MAX_NICKNAME_LENGTH) {
            throw NPGExceptionType.BAD_REQUEST_UNUSABLE_NICKNAME.of("20글자 이하로 입력해주세요.");
        }
    }

    private boolean isDuplicatedNickname(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    private User updateNickname(UserInfoRequestDto requestDto, Long userId) {
        User user = findUserById(userId);
        user.updateNickname(requestDto);

        return user;
    }

    private User findUserById(Long userId){
        return userRepository.findById(userId)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);
    }
}
