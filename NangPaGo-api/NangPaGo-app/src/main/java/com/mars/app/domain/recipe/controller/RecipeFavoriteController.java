package com.mars.app.domain.recipe.controller;

import com.mars.app.domain.recipe.message.favorite.RecipeFavoriteMessagePublisher;
import com.mars.common.dto.page.PageResponseDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.recipe.dto.RecipeListResponseDto;
import com.mars.app.domain.recipe.dto.favorite.RecipeFavoriteResponseDto;
import com.mars.app.domain.recipe.service.RecipeFavoriteService;
import com.mars.common.dto.page.PageRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "레시피 즐겨찾기 API", description = "레시피 즐겨찾기 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
@RestController
public class RecipeFavoriteController {

    private final RecipeFavoriteService recipeFavoriteService;
    private final RecipeFavoriteMessagePublisher recipeFavoriteMessagePublisher;

    @Operation(summary = "즐겨찾기 목록 조회")
    @AuthenticatedUser
    @GetMapping("/favorite/list")
    public ResponseDto<PageResponseDto<RecipeListResponseDto>> getFavoriteRecipes(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeFavoriteService.getFavoriteRecipes(userId, pageRequestVO));
    }

    @Operation(summary = "즐겨찾기 상태 확인")
    @GetMapping("/{id}/favorite/status")
    public ResponseDto<Boolean> isFavorite(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeFavoriteService.isFavorite(id, userId));
    }

    @Operation(summary = "즐겨찾기 상태 변경")
    @AuthenticatedUser
    @PostMapping("/{id}/favorite/toggle")
    public ResponseDto<RecipeFavoriteResponseDto> toggleFavorite(@PathVariable("id") Long id) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeFavoriteMessagePublisher.toggleFavorite(id, userId));
    }
}
