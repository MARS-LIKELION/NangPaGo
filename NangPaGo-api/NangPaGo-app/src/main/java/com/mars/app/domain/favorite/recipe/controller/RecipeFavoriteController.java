package com.mars.app.domain.favorite.recipe.controller;

import com.mars.app.domain.favorite.recipe.message.RecipeFavoriteMessagePublisher;
import com.mars.common.dto.page.PageDto;
import com.mars.common.dto.ResponseDto;
import com.mars.app.aop.auth.AuthenticatedUser;
import com.mars.app.component.auth.AuthenticationHolder;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteListResponseDto;
import com.mars.app.domain.favorite.recipe.dto.RecipeFavoriteResponseDto;
import com.mars.app.domain.favorite.recipe.service.RecipeFavoriteService;
import com.mars.common.dto.page.PageRequestVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "레시피 즐겨찾기 API", description = "레시피 즐겨찾기 관련 API")
@RequiredArgsConstructor
@RequestMapping("/api/recipe")
@RestController
public class RecipeFavoriteController {

    private final RecipeFavoriteService recipeFavoriteService;
    private final RecipeFavoriteMessagePublisher recipeFavoriteMessagePublisher;

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

    @Operation(summary = "즐겨찾기 목록 조회")
    @AuthenticatedUser
    @GetMapping("/favorite/list")
    public ResponseDto<PageDto<RecipeFavoriteListResponseDto>> getFavoriteRecipes(PageRequestVO pageRequestVO) {
        Long userId = AuthenticationHolder.getCurrentUserId();
        return ResponseDto.of(recipeFavoriteService.getFavoriteRecipes(userId, pageRequestVO));
    }
}
