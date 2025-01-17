package com.mars.NangPaGo.domain.comment.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.common.exception.NPGException;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import com.mars.NangPaGo.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import com.mars.NangPaGo.support.IntegrationTestSupport;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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
        Pageable pageable = PageRequest.of(pageNo, pageSize);

        User user = createUser("dummy@nangpago.com");
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

        Page<RecipeComment> commentPage = recipeCommentRepository.findByRecipeId(recipe.getId(), pageable);

        // when
        PageDto<RecipeCommentResponseDto> pageDto = recipeCommentService.pagedCommentsByRecipe(recipe.getId(),
            user.getEmail(), pageNo, pageSize);

        //then
        System.out.println(pageDto.getContent());

        assertThat(pageDto.getTotalPages()).isEqualTo(2);
        assertThat(pageDto.getTotalItems()).isEqualTo(4);
    }

    @Transactional
    @DisplayName("레시피에 댓글 작성한다.")
    @Test
    void create() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");

        userRepository.save(user);
        recipeRepository.save(recipe);

        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto("댓글 작성 예시");

        // when
        RecipeCommentResponseDto responseDto = recipeCommentService.create(requestDto, user.getEmail(), recipe.getId());

        // then
        assertThat(responseDto)
            .extracting("content")
            .isEqualTo("댓글 작성 예시");
        assertThat(responseDto)
            .extracting("email")
            .isEqualTo("dum**@nangpago.com");
    }

    @Transactional
    @DisplayName("레시피 댓글 수정한다.")
    @Test
    void update() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "변경 전 댓글입니다.");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        String updateText = "변경된 댓글입니다.";
        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto(updateText); // 텍스트 변경

        // when
        RecipeCommentResponseDto responseDto = recipeCommentService.update(comment.getId(), user.getEmail(),
            requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto).extracting("content").isEqualTo(updateText);
    }

    @Transactional
    @DisplayName("레시피 댓글 삭제한다.")
    @Test
    void delete() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "댓글");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        // when
        recipeCommentService.delete(comment.getId(), user.getEmail());

        // then
        assertFalse(recipeCommentRepository.existsById(comment.getId()));
    }

    @Transactional
    @DisplayName("다른 유저의 댓글 삭제, 예외를 발생한다.")
    @Test
    void validateOwnershipException() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "댓글");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        String anotherUserEmail = "another@nangpago.com";

        // when, then
        assertThatThrownBy(() -> recipeCommentService.delete(comment.getId(), anotherUserEmail))
            .isInstanceOf(NPGException.class)
            .hasMessage("댓글을 수정/삭제할 권한이 없습니다.");
    }

    @DisplayName("레시피 댓글 작성, 레시피의 ID가 없을 때 예외를 발생한다.")
    @Test
    void validateRecipeException() {
        // given
        User user = createUser("dummy@nangpago.com");
        userRepository.save(user);

        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto("댓글 작성 예시");

        // when, then
        assertThatThrownBy(() -> recipeCommentService.create(requestDto, user.getEmail(), 1L))
            .isInstanceOf(NPGException.class)
            .hasMessage("레시피를 찾을 수 없습니다.");
    }

    @DisplayName("레시피 댓글 작성, 유저의 ID가 없을 때 예외를 발생한다.")
    @Test
    void findUserByEmailException() {
        // given
        Recipe recipe = createRecipe("파스타");
        recipeRepository.save(recipe);

        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto("댓글 작성 예시");

        // when, then
        assertThatThrownBy(() -> recipeCommentService.create(requestDto, "dummy@nangpago.com", recipe.getId()))
            .isInstanceOf(NPGException.class)
            .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Transactional
    @DisplayName("레시피 댓글 수정, 댓글의 ID가 없을 때 예외를 발생한다.")
    @Test
    void validateCommentException() {
        // given
        User user = createUser("dummy@nangpago.com");
        Recipe recipe = createRecipe("파스타");
        RecipeComment comment = createRecipeComment(recipe, user, "변경 전 댓글입니다.");

        userRepository.save(user);
        recipeRepository.save(recipe);
        recipeCommentRepository.save(comment);

        String updateText = "변경된 댓글입니다.";
        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto(updateText); // 텍스트 변경

        // when, then
        assertThatThrownBy(() -> recipeCommentService.update(1L, user.getEmail(), requestDto))
            .isInstanceOf(NPGException.class)
            .hasMessage("댓글을 찾을 수 없습니다.");
    }


    private User createUser(String email) {
        return User.builder()
            .email(email)
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
