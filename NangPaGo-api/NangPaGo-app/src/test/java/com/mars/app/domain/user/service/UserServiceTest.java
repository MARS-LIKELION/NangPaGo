package com.mars.app.domain.user.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mars.app.domain.recipe.dto.comment.RecipeCommentResponseDto;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteListResponseDto;
import com.mars.app.domain.recipe.dto.RecipeResponseDto;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.dto.user.MyPageDto;
import com.mars.common.dto.user.UserInfoRequestDto;
import com.mars.common.dto.user.UserInfoResponseDto;
import com.mars.common.dto.user.UserResponseDto;
import com.mars.common.enums.oauth.OAuth2Provider;
import com.mars.common.exception.NPGException;
import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.common.model.favorite.recipe.RecipeFavorite;
import com.mars.common.model.recipe.Recipe;
import com.mars.common.model.recipe.RecipeLike;
import com.mars.common.model.user.User;
import com.mars.app.domain.recipe.repository.RecipeCommentRepository;
import com.mars.app.domain.recipe.repository.RecipeFavoriteRepository;
import com.mars.app.domain.recipe.repository.RecipeLikeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

@Transactional
class UserServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RecipeLikeRepository recipeLikeRepository;
    @Autowired
    private RecipeFavoriteRepository recipeFavoriteRepository;
    @Autowired
    private RecipeCommentRepository recipeCommentRepository;
    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private UserService userService;

    @DisplayName("현재 사용자 정보를 조회할 수 있다.")
    @Test
    void getCurrentUser() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        // when
        UserResponseDto result = userService.getCurrentUser(user.getId());

        // then
        assertThat(result.nickname()).isEqualTo("테스트 닉네임");
        assertThat(result.email()).isEqualTo(user.getEmail());
        assertThat(result.password()).isNull();  // 응답 DTO에 들어있는 password 는 반드시 null이어야 한다.
    }

    @DisplayName("존재하지 않는 사용자를 조회하면 예외가 발생한다.")
    @Test
    void getCurrentUser_NotFound() {
        // when & then
        assertThatThrownBy(() -> userService.getCurrentUser(999L))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("사용자를 찾을 수 없습니다");
    }

    @DisplayName("유저 정보 수정을 위한 상세 정보를 조회할 수 있다.")
    @Test
    void getUserDetailInfo() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        // when
        UserInfoResponseDto userDetailInfo = userService.getUserDetailInfo(user.getId());

        // then
        assertThat(userDetailInfo.nickname()).isEqualTo("테스트 닉네임");
    }

    @DisplayName("마이페이지 정보를 조회할 수 있다.")
    @Test
    void getMyPage() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        Recipe recipe = createRecipe("테스트 레시피");
        recipeRepository.save(recipe);

        RecipeLike recipeLike = RecipeLike.of(user, recipe);
        RecipeFavorite recipeFavorite = RecipeFavorite.of(user, recipe);
        RecipeComment recipeComment = RecipeComment.create(recipe, user, "맛있어요!");

        recipeLikeRepository.save(recipeLike);
        recipeFavoriteRepository.save(recipeFavorite);
        recipeCommentRepository.save(recipeComment);

        // when
        MyPageDto result = userService.getMyPage(user.getId());

        // then
        assertThat(result.nickName()).isEqualTo(user.getNickname());
        assertThat(result.likeCount()).isEqualTo(1);
        assertThat(result.favoriteCount()).isEqualTo(1);
        assertThat(result.commentCount()).isEqualTo(1);
    }

    @DisplayName("유저가 좋아요한 레시피 리스트를 조회할 수 있다.")
    @Test
    void getMyLikedRecipes() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        Recipe recipe1 = createRecipe("테스트 레시피 1");
        Recipe recipe2 = createRecipe("테스트 레시피 2");
        recipeRepository.saveAll(List.of(recipe1, recipe2));

        RecipeLike recipeLike1 = RecipeLike.of(user, recipe1);
        RecipeLike recipeLike2 = RecipeLike.of(user, recipe2);
        recipeLikeRepository.saveAll(List.of(recipeLike1, recipeLike2));

        PageRequestVO pageRequestVO = PageRequestVO.of(1, 12);

        // when
        PageResponseDto<RecipeResponseDto> myLikedRecipes = userService.getMyLikedRecipes(user.getId(), pageRequestVO);

        // then
        assertThat(myLikedRecipes)
            .extracting(PageResponseDto::getTotalPages, PageResponseDto::getTotalItems)
            .containsExactly(1, 2L);
        assertThat(myLikedRecipes.getContent())
            .extracting(RecipeResponseDto::name)
            .containsExactlyInAnyOrder(
                "테스트 레시피 1",
                "테스트 레시피 2"
            );
    }

    @DisplayName("유저가 즐겨찾기한 레시피 리스트를 조회할 수 있다.")
    @Test
    void getMyFavorites() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        Recipe recipe1 = createRecipe("테스트 레시피 1");
        Recipe recipe2 = createRecipe("테스트 레시피 2");
        recipeRepository.saveAll(List.of(recipe1, recipe2));

        RecipeFavorite recipeFavorite1 = RecipeFavorite.of(user, recipe1);
        RecipeFavorite recipeFavorite2 = RecipeFavorite.of(user, recipe2);
        recipeFavoriteRepository.saveAll(List.of(recipeFavorite1, recipeFavorite2));

        PageRequestVO pageRequestVO = PageRequestVO.of(1, 12);

        // when
        PageResponseDto<RecipeFavoriteListResponseDto> myFavorites = userService.getMyFavorites(user.getId(), pageRequestVO);

        // then
        assertThat(myFavorites)
            .extracting(PageResponseDto::getTotalPages, PageResponseDto::getTotalItems)
            .containsExactly(1, 2L);
        assertThat(myFavorites.getContent())
            .extracting(RecipeFavoriteListResponseDto::name)
            .containsExactlyInAnyOrder(
                "테스트 레시피 1",
                "테스트 레시피 2"
            );
    }

    @DisplayName("유저가 작성한 댓글 리스트를 조회할 수 있다.")
    @Test
    void getMyComments() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        Recipe recipe = createRecipe("테스트 레시피 1");
        recipeRepository.save(recipe);

        RecipeComment recipeComment1 = RecipeComment.create(recipe, user, "테스트 댓글 1");
        RecipeComment recipeComment2 = RecipeComment.create(recipe, user, "테스트 댓글 2");
        recipeCommentRepository.saveAll(List.of(recipeComment1, recipeComment2));

        PageRequestVO pageRequestVO = PageRequestVO.of(1, 12);

        // when
        PageResponseDto<RecipeCommentResponseDto> myComments = userService.getMyComments(user.getId(), pageRequestVO);

        // then
        assertThat(myComments)
            .extracting(PageResponseDto::getTotalPages, PageResponseDto::getTotalItems)
            .containsExactly(1, 2L);
        assertThat(myComments.getContent())
            .extracting(RecipeCommentResponseDto::content)
            .containsExactlyInAnyOrder(
                "테스트 댓글 1",
                "테스트 댓글 2"
            );
    }

    @DisplayName("닉네임 중복 체크를 할 수 있다.")
    @Test
    void isNicknameAvailable() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        // when
        boolean result = userService.isNicknameAvailable("변경할 닉네임");

        // then
        assertThat(result).isTrue();
    }

    @DisplayName("체크하려는 닉네임이 중복인 경우 예외가 발생한다.")
    @Test
    void isNicknameNotAvailable() {
        // given
        User user = createUser("사용중 닉네임");
        userRepository.save(user);

        // when & then
        assertThatThrownBy(() -> userService.isNicknameAvailable("사용중 닉네임"))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("이미 사용중인 닉네임입니다");
    }

    @DisplayName("닉네임을 업데이트할 수 있다.")
    @Test
    void updateUserInfo() {
        // given
        User user = createUser("변경전 닉네임");
        userRepository.save(user);

        UserInfoRequestDto requestDto = new UserInfoRequestDto("변경 후 닉네임");

        // when
        UserInfoResponseDto result = userService.updateUserInfo(requestDto, user.getId());

        // then
        assertThat(result.nickname()).isEqualTo("변경 후 닉네임");
    }

    @DisplayName("변경을 시도하는 닉네임이 20자를 초과하면 예외가 발생한다.")
    @Test
    void updateUserInfo_TooLongNickname() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        UserInfoRequestDto requestDto = new UserInfoRequestDto("a".repeat(21));

        // when & then
        assertThatThrownBy(() -> userService.updateUserInfo(requestDto, user.getId()))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("20글자 이하로 입력해주세요");
    }

    @DisplayName("변경을 시도하는 닉네임이 2자 미만이면 예외가 발생한다.")
    @Test
    void updateUserInfo_TooShortNickname() {
        // given
        User user = createUser("테스트 닉네임");
        userRepository.save(user);

        UserInfoRequestDto requestDto = new UserInfoRequestDto("a");

        // when & then
        assertThatThrownBy(() -> userService.updateUserInfo(requestDto, user.getId()))
            .isInstanceOf(NPGException.class)
            .hasMessageContaining("두글자 이상 입력해주세요.");
    }

    private User createUser(String nickname) {
        return User.builder()
            .email("test@example.com")
            .nickname(nickname)
            .oauth2Provider(OAuth2Provider.GOOGLE)
            .password("패스워드")
            .build();
    }

    private Recipe createRecipe(String name) {
        return Recipe.builder()
            .name(name)
            .mainIngredient("주 식재료")
            .calorie(100)
            .category("레시피 카테고리")
            .cookingMethod("조리방법")
            .manuals(new ArrayList<>())
            .manualImages(new ArrayList<>())
            .build();
    }
}
