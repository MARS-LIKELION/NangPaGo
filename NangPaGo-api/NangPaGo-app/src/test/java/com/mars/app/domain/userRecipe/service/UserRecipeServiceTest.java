package com.mars.app.domain.userRecipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import com.mars.app.domain.userRecipe.dto.UserRecipeRequestDto;
import com.mars.app.domain.userRecipe.dto.UserRecipeResponseDto;
import com.mars.app.domain.userRecipe.repository.UserRecipeRepository;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import com.mars.common.enums.userRecipe.UserRecipeStatus;
import com.mars.common.model.user.User;
import com.mars.common.model.userRecipe.UserRecipe;
import com.mars.common.model.userRecipe.UserRecipeIngredient;
import com.mars.common.model.userRecipe.UserRecipeManual;
import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

@Transactional
class UserRecipeServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRecipeRepository userRecipeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserRecipeService userRecipeService;

    @DisplayName("유저는 본인의 레시피를 작성할 수 있다.")
    @Test
    void createUserRecipe() {
        // given
        User user = User.builder()
            .email("email@example.com")
            .build();
        userRepository.save(user);

        UserRecipeRequestDto requestDto = new UserRecipeRequestDto(
            "레시피 제목",
            "레시피 내용",
            true,
            List.of("재료1", "재료2"),
            List.of("조리법1", "조리법2")

        );

        MockMultipartFile mainFile = new MockMultipartFile("mainFile", "", "image/jpeg", new byte[0]);
        List<MultipartFile> otherFiles = List.of();

        // when
        UserRecipeResponseDto responseDto = userRecipeService.createUserRecipe(requestDto, mainFile, otherFiles, user.getId());

        // then
        assertThat(responseDto.title()).isEqualTo("레시피 제목");
        assertThat(responseDto.content()).isEqualTo("레시피 내용");
        assertThat(responseDto.isPublic()).isTrue();
        assertThat(responseDto.ingredients()).containsExactlyInAnyOrder("재료1 ", "재료2 ");
        assertThat(responseDto.manuals()).containsExactly("1. 조리법1", "2. 조리법2");
        assertThat(responseDto.likeCount()).isEqualTo(0);
        assertThat(responseDto.commentCount()).isEqualTo(0);
        assertThat(responseDto.recipeStatus()).isEqualTo(UserRecipeStatus.ACTIVE.name());
        assertThat(userRecipeRepository.findById(responseDto.id())).isPresent();
    }

    @DisplayName("유저는 본인의 레시피를 수정할 수 있다.")
    @Test
    void updateUserRecipe() {
        // given
        User user = User.builder()
            .email("email@example.com")
            .build();
        userRepository.save(user);

        UserRecipe recipe = UserRecipe.builder()
            .user(user)
            .title("초기 제목")
            .content("초기 내용")
            .mainImageUrl("초기 이미지 URL")
            .isPublic(true)
            .recipeStatus(UserRecipeStatus.ACTIVE)
            .ingredients(new ArrayList<>())
            .manuals(new ArrayList<>())
            .comments(new ArrayList<>())
            .likes(new ArrayList<>())
            .build();

        UserRecipeIngredient ingredient = UserRecipeIngredient.builder()
            .userRecipe(recipe)
            .name("초기 재료")
            .amount("")
            .build();
        recipe.getIngredients().add(ingredient);

        UserRecipeManual manual = UserRecipeManual.builder()
            .userRecipe(recipe)
            .step(1)
            .description("초기 매뉴얼")
            .images(new ArrayList<>())
            .build();
        recipe.getManuals().add(manual);
        userRecipeRepository.save(recipe);

        UserRecipeRequestDto updateDto = new UserRecipeRequestDto(
            "수정 제목",
            "수정 내용",
            false,
            List.of("수정 재료"),
            List.of("수정 매뉴얼")
        );

        MockMultipartFile newMainFile = new MockMultipartFile("mainFile", "newImage.jpg", "image/jpeg", "dummy image content".getBytes());
        List<MultipartFile> newOtherFiles = List.of();

        // when
        UserRecipeResponseDto updatedResponse = userRecipeService.updateUserRecipe(recipe.getId(), updateDto, newMainFile, newOtherFiles, user.getId());

        // then
        assertThat(updatedResponse.title()).isEqualTo("수정 제목");
        assertThat(updatedResponse.content()).isEqualTo("수정 내용");
        assertThat(updatedResponse.isPublic()).isFalse();
        assertThat(updatedResponse.mainImageUrl()).isNotEqualTo("초기 이미지 URL");
        assertThat(updatedResponse.ingredients()).containsExactly("수정 재료 ");
        assertThat(updatedResponse.manuals()).containsExactly("1. 수정 매뉴얼");
        assertThat(updatedResponse.likeCount()).isEqualTo(0);
        assertThat(updatedResponse.commentCount()).isEqualTo(0);

        UserRecipe updatedRecipe = userRecipeRepository.findById(recipe.getId()).orElseThrow();
        assertThat(updatedRecipe.getTitle()).isEqualTo("수정 제목");
        assertThat(updatedRecipe.getContent()).isEqualTo("수정 내용");
        assertThat(updatedRecipe.isPublic()).isFalse();
    }

    @DisplayName("유저는 본인의 레시피를 삭제할 수 있다.")
    @Test
    void deleteUserRecipe() {
        // given
        User user = User.builder()
            .email("email@example.com")
            .build();
        userRepository.save(user);

        UserRecipe recipe = UserRecipe.builder()
            .user(user)
            .title("삭제 제목")
            .content("삭제 내용")
            .mainImageUrl("삭제 이미지 URL")
            .isPublic(true)
            .recipeStatus(UserRecipeStatus.ACTIVE)
            .ingredients(new ArrayList<>())
            .manuals(new ArrayList<>())
            .comments(new ArrayList<>())
            .likes(new ArrayList<>())
            .build();
        userRecipeRepository.save(recipe);

        // when
        userRecipeService.deleteUserRecipe(recipe.getId(), user.getId());

        // then
        UserRecipe deletedRecipe = userRecipeRepository.findById(recipe.getId()).orElseThrow();
        assertThat(deletedRecipe.getRecipeStatus()).isEqualTo(UserRecipeStatus.DELETED);
    }
}
