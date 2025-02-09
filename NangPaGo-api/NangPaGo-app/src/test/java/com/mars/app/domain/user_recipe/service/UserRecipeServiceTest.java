package com.mars.app.domain.user_recipe.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.mars.app.domain.user_recipe.dto.UserRecipeRequestDto;
import com.mars.app.domain.user_recipe.dto.UserRecipeResponseDto;
import com.mars.app.domain.user_recipe.repository.UserRecipeRepository;
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
        User user = User.builder().email("email@example.com").build();
        userRepository.save(user);

        UserRecipeRequestDto requestDto = UserRecipeRequestDto.builder()
            .title("레시피 제목")
            .content("레시피 내용")
            .isPublic(true)
            .mainImageUrl("")
            .ingredients(List.of(
                UserRecipeRequestDto.IngredientDto.builder().name("재료1").amount("1개").build()
            ))
            .manuals(List.of(
                UserRecipeRequestDto.ManualDto.builder().step(1).description("조리법1").imageUrl("").build()
            ))
            .build();

        MockMultipartFile mainFile = new MockMultipartFile("mainFile", "image.jpg", "image/jpeg", new byte[10]);
        List<MultipartFile> otherFiles = List.of(new MockMultipartFile("otherFile", "other.jpg", "image/jpeg", new byte[10]));

        UserRecipeResponseDto responseDto = userRecipeService.createUserRecipe(requestDto, mainFile, otherFiles, user.getId());

        assertThat(responseDto.title()).isEqualTo("레시피 제목");
        assertThat(responseDto.content()).isEqualTo("레시피 내용");
        assertThat(responseDto.isPublic()).isTrue();
        assertThat(responseDto.ingredients()).extracting("name").containsExactly("재료1");
        assertThat(responseDto.manuals()).extracting("description").containsExactly("조리법1");
        assertThat(responseDto.likeCount()).isEqualTo(0);
        assertThat(responseDto.commentCount()).isEqualTo(0);
        assertThat(responseDto.recipeStatus()).isEqualTo(UserRecipeStatus.ACTIVE.name());
        assertThat(userRecipeRepository.findById(responseDto.id())).isPresent();
    }

    @DisplayName("유저는 본인의 레시피를 수정할 수 있다.")
    @Test
    void updateUserRecipe() {
        User user = User.builder().email("email@example.com").build();
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
            .amount("1개")
            .build();
        recipe.getIngredients().add(ingredient);

        UserRecipeManual manual = UserRecipeManual.builder()
            .userRecipe(recipe)
            .step(1)
            .description("초기 매뉴얼")
            .imageUrl("초기 매뉴얼 이미지 URL")
            .build();
        recipe.getManuals().add(manual);
        userRecipeRepository.save(recipe);

        UserRecipeRequestDto updateDto = UserRecipeRequestDto.builder()
            .title("수정 제목")
            .content("수정 내용")
            .isPublic(false)
            .mainImageUrl("")
            .ingredients(List.of(
                UserRecipeRequestDto.IngredientDto.builder().name("수정 재료").amount("2개").build()
            ))
            .manuals(List.of(
                UserRecipeRequestDto.ManualDto.builder().step(1).description("수정 매뉴얼").imageUrl("").build()
            ))
            .build();

        MockMultipartFile newMainFile = new MockMultipartFile("mainFile", "newImage.jpg", "image/jpeg", "dummy image content".getBytes());
        List<MultipartFile> newOtherFiles = List.of(new MockMultipartFile("otherFile", "newOther.jpg", "image/jpeg", "dummy other image".getBytes()));

        UserRecipeResponseDto updatedResponse = userRecipeService.updateUserRecipe(recipe.getId(), updateDto, newMainFile, newOtherFiles, user.getId());

        assertThat(updatedResponse.title()).isEqualTo("수정 제목");
        assertThat(updatedResponse.content()).isEqualTo("수정 내용");
        assertThat(updatedResponse.isPublic()).isFalse();
        assertThat(updatedResponse.mainImageUrl()).isNotEqualTo("초기 이미지 URL");
        assertThat(updatedResponse.ingredients()).extracting("name").containsExactly("수정 재료");
        assertThat(updatedResponse.manuals()).extracting("description").containsExactly("수정 매뉴얼");
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
        User user = User.builder().email("email@example.com").build();
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

        userRecipeService.deleteUserRecipe(recipe.getId(), user.getId());

        UserRecipe deletedRecipe = userRecipeRepository.findById(recipe.getId()).orElseThrow();
        assertThat(deletedRecipe.getRecipeStatus()).isEqualTo(UserRecipeStatus.DELETED);
    }
}
