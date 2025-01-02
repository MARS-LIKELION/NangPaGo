package com.mars.NangPaGo.domain.comment.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.NangPaGo.common.dto.PageDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import com.mars.NangPaGo.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ExtendWith(MockitoExtension.class)
class RecipeCommentServiceTest {

    @Mock
    private RecipeCommentRepository recipeCommentRepository;
    @Mock
    private RecipeRepository recipeRepository;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RecipeCommentService recipeCommentService;

    private long recipeId;
    private String email;
    private String content;

    private Recipe recipe;
    private User user = User.builder()
        .email(email)
        .build();
    private RecipeComment comment;

    @BeforeEach
    public void setUp() {
        // given
        recipeId = 1L;
        email = "dummy@nangpago.com";
        content = "이 요리는 끔찍해요. 최악이에요";

        recipe = new Recipe();

        user = User.builder()
            .email(email)
            .build();

        comment = RecipeComment.create(recipe, user, content);
    }

    @DisplayName("레시피 댓글 Page 조회")
    @Test
    void pagedCommentsByRecipe() {
        // given
        int pageNo = 0;
        int pageSize = 3;
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        List<RecipeComment> comments = new ArrayList<>();

        RecipeComment comment1 = RecipeComment.create(recipe, user, "두번째 레시피 내용");
        RecipeComment comment2 = RecipeComment.create(recipe, user, "세번째 레시피 내용");
        RecipeComment comment3 = RecipeComment.create(recipe, user, "네번째 레시피 내용");

        comments.add(comment);
        comments.add(comment1);
        comments.add(comment2);
        comments.add(comment3);

        Page<RecipeComment> page = new PageImpl<>(comments.subList(0, 2), pageable, comments.size());

        //mocking
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.ofNullable(recipe));
        when(recipeCommentRepository.findByRecipeId(anyLong(),
            any(PageRequest.class))).thenReturn(page);

        // when
        PageDto<RecipeCommentResponseDto> pageDto = recipeCommentService.PagedCommentsByRecipe(anyLong(), pageNo,
            pageSize);

        //then
        System.out.println(pageDto.getContent());
        assertThat(pageDto.getTotalPages()).isEqualTo(2);
        assertThat(pageDto.getTotalItems()).isEqualTo(4);
    }

    @DisplayName("레시피에 댓글 작성")
    @Test
    void create() {
        // given
        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto(recipeId, email, content);

        // mocking
        when(recipeRepository.findById(anyLong())).thenReturn(Optional.of(recipe));
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(recipeCommentRepository.save(any())).thenReturn(comment);

        // when
        RecipeCommentResponseDto responseDto = recipeCommentService.create(requestDto, recipeId);

        // then
        assertThat(responseDto).isNotNull();
        verify(recipeCommentRepository, times(1)).save(any(RecipeComment.class));
    }

    @DisplayName("레시피 댓글 수정")
    @Test
    void update() {
        // given
        long commentId = anyLong();
        assertEquals(content, comment.getContent()); // 수정 전 텍스트 확인

        String updateText = "변경된 텍스트 내용입니다.";
        RecipeCommentRequestDto requestDto = new RecipeCommentRequestDto(recipeId, email, updateText); // 텍스트 변경

        // mocking
        when(recipeCommentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        // when
        comment.updateText(updateText);
        RecipeCommentResponseDto responseDto = recipeCommentService.update(commentId, requestDto);

        // then
        assertThat(responseDto).isNotNull();
        assertThat(responseDto).extracting("content").isEqualTo(updateText);
    }

    @DisplayName("레시피 댓글 삭제")
    @Test
    void delete() {
        // given
        long commentId = anyLong();

        // mocking
        when(recipeCommentRepository.findById(commentId)).thenReturn(Optional.ofNullable(comment));

        // when
        recipeCommentService.delete(commentId);

        // then
        verify(recipeCommentRepository, times(1)).delete(any(RecipeComment.class));
        assertFalse(recipeCommentRepository.existsById(commentId));
    }
}
