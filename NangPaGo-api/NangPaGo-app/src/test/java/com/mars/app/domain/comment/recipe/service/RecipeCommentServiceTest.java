package com.mars.app.domain.comment.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.mars.common.dto.page.PageDto;
import com.mars.common.exception.NPGException;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.app.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.common.model.comment.recipe.RecipeComment;
import com.mars.app.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.common.model.recipe.Recipe;
import com.mars.app.domain.recipe.repository.RecipeRepository;
import com.mars.common.model.user.User;
import com.mars.app.domain.user.repository.UserRepository;
import com.mars.app.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class RecipeCommentServiceTest extends IntegrationTestSupport {

    @Autowired
    private RecipeCommentRepository recipeCommentRepository;
    @Autowired
    private RecipeRepository recipeRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecipeCommentService recipeCommentService;

    @AfterEach
    void tearDown() {
        recipeCommentRepository.deleteAllInBatch();
        recipeRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @DisplayName("레시피의 모든 댓글을 조회한다.")
    @Test
    void pagedCommentsByRecipe() {
        // given
        int pageNo = 0;
        int pageSize = 3;

        User user = createUser("dummy@nangpago.com", "TestNickName");
        Recipe recipe = createRecipe("파스타");
        List<RecipeComment> comments = Arrays.asList(
            createRecipeComment(recipe, user, "1번째 댓글"),
            createRecipeComment(recipe, user, "2번째 댓글"),
            createRecipeComment(recipe, user, "3번째 댓글"),
            createRecipeComment(recipe, user, "4번째 댓글")
        );

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.saveAll(comments);

        // when
        PageDto<RecipeCommentResponseDto> pageDto = recipeCommentService.pagedCommentsByRecipe(recipe.getId(),
            user.getId(), pageNo, pageSize);

        //then
        assertThat(pageDto)
            .extracting(PageDto::getTotalPages, PageDto::getTotalItems)
            .containsExactly(2, 4L);
    }

    @DisplayName("레시피에 댓글을 작성할 수 있다.")
    @Test
    void create() {
        // given
        User user = createUser("dummy@nangpago.com", "TestNickName");
        Recipe recipe = createRecipe("파스타");

        userRepository.save(user);
        recipeRepository.save(recipe);

        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto("댓글 작성 예시");

        // when
        RecipeCommentResponseDto responseDto = recipeCommentService.create(requestDto, user.getId(), recipe.getId());

        // then
        assertThat(responseDto)
            .extracting("content", "writerName")
            .containsExactly("댓글 작성 예시", "TestNickName");
    }

    @DisplayName("레시피 댓글을 수정할 수 있다.")
    @Test
    void update() {
        // given
        User user = createUser("dummy@nangpago.com", "TestNickName");
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "변경 전 댓글입니다.");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        String updateText = "변경된 댓글입니다.";
        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto(updateText); // 텍스트 변경

        // when
        RecipeCommentResponseDto responseDto = recipeCommentService.update(comment.getId(), user.getId(),
            requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto).extracting("content").isEqualTo(updateText);
    }

    @DisplayName("레시피 댓글을 삭제할 수 있다.")
    @Test
    void delete() {
        // given
        User user = createUser("dummy@nangpago.com", "TestNickName");
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "댓글");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        // when
        recipeCommentService.delete(comment.getId(), user.getId());

        // then
        assertThat(recipeCommentRepository.existsById(comment.getId()))
            .isFalse();
    }

    @DisplayName("다른 유저의 댓글을 삭제할 때 예외를 발생시킬 수 있다.")
    @Test
    void validateOwnershipException() {
        // given
        User user = createUser("dummy@nangpago.com", "TestNickName");
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "댓글");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        Long anotherUserId = 9999L;

        // when, then
        assertThatThrownBy(() -> recipeCommentService.delete(comment.getId(), anotherUserId))
            .isInstanceOf(NPGException.class)
            .hasMessage("댓글을 수정/삭제할 권한이 없습니다.");
    }

    @DisplayName("레시피를 찾을 수 없을 때 예외를 발생시킬 수 있다.")
    @Test
    void validateRecipeException() {
        // given
        User user = createUser("dummy@nangpago.com", "TestNickName");
        userRepository.save(user);

        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto("댓글 작성 예시");

        // when, then
        assertThatThrownBy(() -> recipeCommentService.create(requestDto, user.getId(), 1L))
            .isInstanceOf(NPGException.class)
            .hasMessage("레시피를 찾을 수 없습니다.");
    }

    @DisplayName("사용자를 찾을 수 없을 때 예외를 발생시킬 수 있다.")
    @Test
    void findUserByEmailException() {
        // given
        Recipe recipe = createRecipe("파스타");
        recipeRepository.save(recipe);

        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto("댓글 작성 예시");

        // when, then
        assertThatThrownBy(() -> recipeCommentService.create(requestDto, 9999L, recipe.getId()))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @DisplayName("레시피 댓글을 수정할 때 댓글을 찾을 수 없을 때 예외를 발생시킬 수 있다.")
    @Test
    void validateCommentException() {
        // given
        User user = createUser("dummy@nangpago.com", "TestNickName");;
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "변경 전 댓글입니다.");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        String updateText = "변경된 댓글입니다.";
        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto(updateText); // 텍스트 변경

        // when, then
        assertThatThrownBy(() -> recipeCommentService.update(1L, user.getId(), requestDto))
            .isInstanceOf(NPGException.class)
            .hasMessage("댓글을 찾을 수 없습니다.");
    }

    @DisplayName("레시피의 댓글 총 개수를 조회할 수 있다.")
    @Test
    void getCommentsCount() {
        // given
        User user = createUser("dummy@nangpago.com", "dummy user");
        Recipe recipe = createRecipe("파스타");
        userRepository.save(user);
        recipeRepository.save(recipe);

        recipeCommentRepository.saveAll(List.of(
            createRecipeComment(recipe, user, "댓글 1"),
            createRecipeComment(recipe, user, "댓글 2"),
            createRecipeComment(recipe, user, "댓글 3")
        ));

        // when
        int result = recipeCommentService.countCommentsByRecipe(recipe.getId());

        // then
        assertThat(result).isEqualTo(3);
    }

    private User createUser(String email, String writerName) {
        return User.builder()
            .email(email)
            .nickname(writerName)
            .build();
    }

    private Recipe createRecipe(String name) {
        return Recipe.builder()
            .name(name)
            .build();
    }

    private RecipeComment createRecipeComment(Recipe recipe, User user, String comment) {
        return RecipeComment.create(recipe, user, comment);
    }
}
