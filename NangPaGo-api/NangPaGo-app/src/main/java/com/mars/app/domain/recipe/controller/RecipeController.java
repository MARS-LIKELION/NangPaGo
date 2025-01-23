package com.mars.app.domain.recipe.controller;

import com.mars.app.domain.recipe.messaging.SseEmitterService;
import com.mars.app.domain.recipe.service.RecipeLikeMessageService;
import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.common.exception.NPGExceptionType;
import com.mars.app.domain.recipe.dto.RecipeEsResponseDto;
import com.mars.app.domain.recipe.dto.RecipeLikeResponseDto;
import com.mars.app.domain.recipe.dto.RecipeResponseDto;
import com.mars.app.domain.recipe.service.RecipeEsService;
import com.mars.app.domain.recipe.service.RecipeEsSynchronizerService;
import com.mars.app.domain.recipe.service.RecipeLikeService;
import com.mars.app.domain.recipe.service.RecipeService;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    private final RecipeLikeMessageService recipeLikeMessageService;
    private final RecipeEsService recipeEsService;
    private final RecipeEsSynchronizerService recipeEsSynchronizerService;
    private final SseEmitterService sseEmitterService;

    @GetMapping("/{id}")
    public ResponseDto<RecipeResponseDto> recipeById(@PathVariable("id") Long id) {
        return ResponseDto.of(recipeService.recipeById(id));
    }

    @AuthenticatedUser
    @GetMapping("/{id}/like/status")
    public ResponseDto<Boolean> getRecipeLikeStatus(@PathVariable Long id) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(recipeLikeService.isLiked(id, email));
    }

    @AuthenticatedUser
    @PostMapping("/{id}/like/toggle")
    public ResponseDto<RecipeLikeResponseDto> toggleRecipeLike(@PathVariable Long id) {
        String email = AuthenticationHolder.getCurrentUserEmail();
        return ResponseDto.of(recipeLikeMessageService.toggleLike(id, email));
    }

    @GetMapping("/{id}/like/count")
    public ResponseDto<Integer> getLikeCount(@PathVariable Long id) {
        return ResponseDto.of(recipeLikeService.getLikeCount(id));
    }

    @GetMapping("/{id}/like/notification/subscribe")
    public SseEmitter streamLikes(@PathVariable Long id) {
        return sseEmitterService.createEmitter(id);
    }

    @GetMapping("/search")
    public ResponseDto<Page<RecipeEsResponseDto>> searchRecipes(
        @RequestParam(name = "pageNo", defaultValue = "1") int pageNo,
        @RequestParam(name = "pageSize", defaultValue = "12") int pageSize,
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "searchType", defaultValue = "INGREDIENTS") String searchType) {

        if (pageNo < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_NO.of();
        }
        if (pageSize < 1) {
            throw NPGExceptionType.BAD_REQUEST_INVALID_PAGE_SIZE.of();
        }

        return ResponseDto.of(recipeEsService.searchRecipes(pageNo - 1, pageSize, keyword, searchType));
    }

    @PostMapping("/bulk-upload/mysql")
    public ResponseDto<String> syncMysql() {
        return ResponseDto.of(recipeEsSynchronizerService.insertRecipeFromMysql(), "MySQL 데이터를 Elastic에 성공적으로 동기화했습니다");
    }
}
