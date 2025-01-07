package com.mars.NangPaGo.domain.user.service;

import com.mars.NangPaGo.common.exception.NPGExceptionType;
import com.mars.NangPaGo.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.NangPaGo.domain.favorite.recipe.repository.RecipeFavoriteRepository;
import com.mars.NangPaGo.domain.recipe.repository.RecipeLikeRepository;
import com.mars.NangPaGo.domain.refrigerator.repository.RefrigeratorRepository;
import com.mars.NangPaGo.domain.user.dto.MyPageDto;
import com.mars.NangPaGo.domain.user.dto.UserInfoRequestDto;
import com.mars.NangPaGo.domain.user.dto.UserInfoResponseDto;
import com.mars.NangPaGo.domain.user.dto.UserResponseDto;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class UserService {

    public static final int MIN_NICKNAME_LENGTH = 1;

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
        int refrigeratorCount = refrigeratorRepository.countByUser(user);

        return MyPageDto.of(
            user,
            likeCount,
            favoriteCount,
            commentCount,
            refrigeratorCount
        );
    }

    public UserInfoResponseDto getUserInfo(String email) {
        return UserInfoResponseDto.from(findUserByEmail(email));
    }

    public boolean usableNickname(String nickname) {
        return !userRepository.existsByNickname(nickname) && nickname.length() > MIN_NICKNAME_LENGTH;
    }

    @Transactional
    public UserInfoResponseDto updateUserInfo(UserInfoRequestDto requestDto, String email) {
        usableNickname(requestDto);

        return UserInfoResponseDto.from(updateNickname(requestDto, email));
    }

    private void usableNickname(UserInfoRequestDto requestDto){
        if (!usableNickname(requestDto.nickname())) {
            throw NPGExceptionType.BAD_REQUEST_UNUSABLE_NICKNAME.of();
        }
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
