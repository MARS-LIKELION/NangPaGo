package com.mars.app.domain.recipe.controller;

import com.mars.app.domain.recipe.message.RecipeLikeMessagePublisher;
import com.mars.app.domain.recipe.event.RecipeLikeSseService;
import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.dto.page.PageRequestVO;
import com.mars.common.exception.NPGExceptionType;
import com.mars.app.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.dto.RecipeResponseDto;
import com.mars.app.domain.recipe.service.RecipeEsService;
import com.mars.app.domain.recipe.service.RecipeEsSynchronizerService;
import com.mars.app.domain.recipe.service.RecipeLikeService;
import com.mars.app.domain.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RequiredArgsConstructor
@Tag(name = "레시피 API", description = "레시피 관련 API")
@RequestMapping("/api/recipe")
@RestController
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeLikeService recipeLikeService;
    private final RecipeEsService recipeEsService;
    private final RecipeEsSynchronizerService recipeEsSynchronizerService;
    private final RecipeLikeMessagePublisher recipeLikeMessagePublisher;
    private final RecipeLikeSseService recipeLikeSseService;

    @GetMapping("/{id}")
    public ResponseDto<RecipeResponseDto> recipeById(@PathVariable("id") Long id) {
        return ResponseDto.of(recipeService.recipeById(id));
    }

    @AuthenticatedUser
    @GetMapping("/{id}/like/status")
    public ResponseDto<Boolean> getRecipeLikeStatus(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeLikeService.isLiked(id, userId));
    }

    @AuthenticatedUser
    @PostMapping("/{id}/like/toggle")
    public ResponseDto<RecipeLikeResponseDto> toggleRecipeLike(@PathVariable Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeLikeMessagePublisher.toggleLike(id, userId));
    }

    @GetMapping("/{id}/like/count")
    public ResponseDto<Integer> getLikeCount(@PathVariable Long id) {
        return ResponseDto.of(recipeLikeService.getLikeCount(id));
    }

    @GetMapping("/{id}/like/notification/subscribe")
    public SseEmitter streamLikes(@PathVariable Long id) {
        return recipeLikeSseService.createEmitter(id);
    }

    @GetMapping("/search")
    public ResponseDto<Page<RecipeEsResponseDto>> searchRecipes(
        @Valid PageRequestVO pageRequestVO,
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "searchType", defaultValue = "INGREDIENTS") String searchType) {

        return ResponseDto.of(recipeEsService.searchRecipes(pageRequestVO, keyword, searchType));
    }

    @PostMapping("/bulk-upload/mysql")
    public ResponseDto<String> syncMysql() {
        return ResponseDto.of(recipeEsSynchronizerService.insertRecipeFromMysql(), "MySQL 데이터를 Elastic에 성공적으로 동기화했습니다");
    }
}
