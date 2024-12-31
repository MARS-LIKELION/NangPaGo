package com.mars.NangPaGo.domain.comment.recipe.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentRequestDto;
import com.mars.NangPaGo.domain.comment.recipe.dto.RecipeCommentResponseDto;
import com.mars.NangPaGo.domain.comment.recipe.entity.RecipeComment;
import com.mars.NangPaGo.domain.comment.recipe.repository.RecipeCommentRepository;
import com.mars.NangPaGo.domain.recipe.entity.Recipe;
import com.mars.NangPaGo.domain.recipe.repository.RecipeRepository;
import com.mars.NangPaGo.domain.user.entity.User;
import com.mars.NangPaGo.domain.user.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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

    @Test
    void pagedCommentsByRecipe() {

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
    
    @Test
    void update() {

    }

    @Test
    void delete() {

    }
}
