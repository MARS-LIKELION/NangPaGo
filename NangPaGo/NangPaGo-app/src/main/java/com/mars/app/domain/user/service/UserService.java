package com.mars.app.domain.user.service;

import com.mars.app.common.dto.PageDto;
import com.mars.app.common.exception.NPGExceptionType;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.app.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.app.domain.favorite.recipe.repository.RecipeFavoriteRepository;
import com.mars.app.domain.recipe.dto.RecipeResponseDto;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.app.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.app.domain.user.dto.MyPageDto;
import com.mars.app.domain.user.dto.UserInfoRequestDto;
import com.mars.app.domain.user.dto.UserInfoResponseDto;
import com.mars.app.domain.user.dto.UserResponseDto;
import com.mars.app.domain.user.entity.User;
import com.mars.app.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
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
    private final RefrigeratorRepository refrigeratorRepository;

    public UserResponseDto getCurrentUser(String email) {
        return UserResponseDto.from(userRepository.findByEmail(email)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of));
    }

    public MyPageDto getMyPage(String email) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(NPGExceptionType.NOT_FOUND_USER::of);

        int likeCount = recipeLikeRepository.countByUser(user);
        int favoriteCount = recipeFavoriteRepository.countByUser(user);
        int commentCount = recipeCommentRepository.countByUser(user);

        return MyPageDto.of(
            user,
            likeCount,
            favoriteCount,
            commentCount
        );
    }

    public PageDto<RecipeResponseDto> getMyLikedRecipes(String email, int pageNo, int pageSize) {
        return PageDto.of(
            recipeLikeRepository.findRecipeLikeByUser(findUserByEmail(email), PageRequest.of(pageNo, pageSize))
                .map(recipeLike -> RecipeResponseDto.from(recipeLike.getRecipe()))
        );
    }

    public PageDto<RecipeFavoriteListResponseDto> getMyFavorites(String email, int pageNo, int pageSize) {
        return PageDto.of(
            recipeFavoriteRepository.findAllByUser(findUserByEmail(email), PageRequest.of(pageNo, pageSize))
                .map(recipeFavorite -> RecipeFavoriteListResponseDto.of(recipeFavorite.getRecipe()))
        );
    }

    public PageDto<RecipeCommentResponseDto> getMyComments(String email, int pageNo, int pageSize) {
        return PageDto.of(
            recipeCommentRepository.findByUserEmailWithRecipe(email, PageRequest.of(pageNo, pageSize))
                .map(recipeComment -> {
                    return RecipeCommentResponseDto.from(recipeComment, recipeComment.getRecipe(), email);
                })
        );
    }

    public UserInfoResponseDto getUserInfo(String email) {
        return UserInfoResponseDto.from(findUserByEmail(email));
    }

    public boolean isNicknameAvailable(String targetNickname) {
        validateNickname(targetNickname);
        return true;
    }

    @Transactional
    public UserInfoResponseDto updateUserInfo(UserInfoRequestDto requestDto, String email) {
        String targetNickname = requestDto.nickname().trim();
        validateNickname(targetNickname);

        User updatedUser = updateNickname(requestDto, email);
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

    private User updateNickname(UserInfoRequestDto requestDto, String email) {
        User user = findUserByEmail(email);
        user.updateNickname(requestDto);

        return user;
    }

    private User findUserByEmail(String email){
        return userRepository.findByEmail(email)
            .orElseThrow(NPGExceptionType.UNAUTHORIZED_NO_AUTHENTICATION_CONTEXT::of);
    }
}
